package org.archer.mq.constants;

public interface ExceptionMsgPatterns {
    String TARGET_TOPIC_NOT_EXIST = "Message whose id is {0} is discarded cause the specified topic {1} does not exist.";

    String TASK_FAILED_TIMES = "task has been failed {0} times";


}
