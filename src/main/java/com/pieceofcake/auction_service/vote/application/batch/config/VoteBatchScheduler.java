package com.pieceofcake.auction_service.vote.application.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job closeVoteJob;

    @Scheduled(cron = "1 0 * * * *") // 매 정각 1초마다
    public void runVoteCloseJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 매번 고유 JobParameter 부여
                    .toJobParameters();

            jobLauncher.run(closeVoteJob, params);
        } catch (Exception e) {
            // 에러 로깅 또는 슬랙 알림
            System.err.println("투표 종료 배치 실행 실패: " + e.getMessage());
        }
    }
}
