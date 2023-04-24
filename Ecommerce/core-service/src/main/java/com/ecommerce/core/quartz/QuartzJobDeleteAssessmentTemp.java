package com.ecommerce.core.quartz;

import com.ecommerce.core.util.FileUploadUtil;
import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.entities.Assessment;
import com.ecommerce.core.service.AssessmentService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;

public class QuartzJobDeleteAssessmentTemp extends QuartzJobBean {

    @Autowired
    AssessmentService service;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        List<Assessment> assessmentTemp = service.getAssessmentTemp();
        String path = ConstantDefine.FILE_PATH.ASSESSMENT + "/";
        for(int i=0; i<assessmentTemp.size(); i++){
            FileUploadUtil.removeDir(path + assessmentTemp.get(i).getOrderAss());
            try {
                service.delete(assessmentTemp.get(i).getId());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("Job run: " + new Date());
    }
}
