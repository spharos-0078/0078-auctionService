package com.pieceofcake.auction_service.vote.application.batch.config;

import com.pieceofcake.auction_service.auction.application.AuctionService;
import com.pieceofcake.auction_service.auction.infrastructure.client.AuctionFeignClient;
import com.pieceofcake.auction_service.kafka.producer.KafkaProducer;
import com.pieceofcake.auction_service.vote.application.batch.processor.VoteCloseProcessor;
import com.pieceofcake.auction_service.vote.entity.Vote;
import com.pieceofcake.auction_service.vote.infrastructure.VoteDetailRepository;
import com.pieceofcake.auction_service.vote.infrastructure.VoteRepository;
import com.pieceofcake.auction_service.vote.infrastructure.client.PieceFeignClient;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Component
public class VoteCloseBatchConfig {

    // Spring Batch 기본 구성 요소
    private final JobRepository jobRepository;                        // Job 및 Step의 메타데이터 저장소
    private final PlatformTransactionManager transactionManager;      // 트랜잭션 처리기
    private final EntityManagerFactory entityManagerFactory;          // JPA 사용을 위한 EntityManager

    // 비즈니스 로직에 필요한 Repository, FeignClient
    private final VoteRepository voteRepository;
    private final VoteDetailRepository voteDetailRepository;
    private final PieceFeignClient pieceFeignClient;
    private final AuctionService auctionService;
    private final AuctionFeignClient auctionFeignClient;
    private final KafkaProducer kafkaProducer;

    /**
     * 배치 잡 정의 - closeVoteJob
     * 하나의 Step만 존재: closeVoteStep
     */
    @Bean
    public Job closeVoteJob() {
        return new JobBuilder("closeVoteJob", jobRepository)
                .start(closeVoteStep())
                .build();
    }

    @Bean
    public VoteCloseProcessor voteCloseProcessor() {
        // 생성자에 voteDetailRepo, feignClient, kafkaProducer 모두 전달하도록 수정
        return new VoteCloseProcessor(voteDetailRepository, pieceFeignClient, kafkaProducer, auctionService, auctionFeignClient);
    }

    /**
     * Step 정의 - closeVoteStep
     * 종료된 투표를 읽고, 투표 상태를 결정하여 DB에 저장
     */
    @Bean
    public Step closeVoteStep() {
        return new StepBuilder("closeVoteStep", jobRepository)
                .<Vote, Vote>chunk(10, transactionManager)     // 한 번에 10개씩 처리
                .reader(expiredVoteReader())                            // Reader 등록 그대로
                .processor(voteCloseProcessor())                        // Processor 에 빈 사용
                .writer(voteRepository::saveAll)                        // Writer 등록 그대로
                .listener(voteCloseProcessor())                         // afterWrite() 호출을 위해 listener 등록
                .build();
    }

    /**
     * Reader 정의
     * 상태가 OPEN이고, endDate가 현재 시각 이전인 투표를 JPA로 조회
     */
    @Bean
    public JpaCursorItemReader<Vote> expiredVoteReader() {
        return new JpaCursorItemReaderBuilder<Vote>()
                .name("expiredVoteReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT v FROM Vote v WHERE v.status = 'OPEN' AND v.endDate < CURRENT_TIMESTAMP")
                .build();
    }
}
