package com.hqh.quizserver.schedule;

import com.hqh.quizserver.constant.Constant;
import com.hqh.quizserver.entity.UserMark;
import com.hqh.quizserver.enumeration.QuizStatus;
import com.hqh.quizserver.repository.UserMarkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ScheduledTasks {

    private final UserMarkRepository userMarkRepository;

    @Autowired
    public ScheduledTasks(UserMarkRepository userMarkRepository) {
        this.userMarkRepository = userMarkRepository;
    }

    // @Scheduled(fixedDelay = 10000)
    @Scheduled(cron = "0 0 * 1 * *")
    public void scheduleUpdateScoreQuiz() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);

        Date startTime = cal.getTime();
        Date endTime = new Date();

        List<UserMark> userMarkList = userMarkRepository.findAllUserMark(startTime, endTime);
        log.info("Start time:: {} End time:: {}", startTime, endTime);

        if (!userMarkList.isEmpty()) {
            log.info("System to control unfinished articles");
            userMarkList.forEach(e -> {
                e.setMark(0);
                e.setFinishedAt(Instant.now());
                e.setCompletedDate(Instant.now());
                e.setUpdatedAt(new Date());
                e.setStatus(String.valueOf(QuizStatus.COMPLETED));
                e.setUpdatedBy(Constant.SYSTEM);
                e.setByPass(Constant.STATUS_FAIL);

                userMarkRepository.save(e);
            });
        } else {
            log.info("There are no items in this time period");
        }
        log.info("SCHEDULE finished ::");
    }
}
