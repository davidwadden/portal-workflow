package io.pivotal.cnde.portal.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JobService {

  private static final Logger logger = LoggerFactory.getLogger(JobService.class);

  public void submitJob() {
    logger.info("submitting job");
  }
}
