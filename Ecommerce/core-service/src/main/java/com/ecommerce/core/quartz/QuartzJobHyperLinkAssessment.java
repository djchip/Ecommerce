package com.ecommerce.core.quartz;

import com.ecommerce.core.service.AssessmentService;
import com.ecommerce.core.service.ProofService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

public class QuartzJobHyperLinkAssessment extends QuartzJobBean {

    @Autowired
    AssessmentService assessmentService;

    @Autowired
    ProofService proofService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Job run: " + new Date());
    }
}
