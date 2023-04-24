package com.ecommerce.core.config;

import com.ecommerce.core.quartz.QuartzService;
import com.ecommerce.core.quartz.QuartzJobDeleteAssessmentTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ecommerce.core.constants.ConstantDefine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitBean {
	
	@Value("${exhibition-path}")
    public String exhibition;
	
	@Value("${collection-path}")
    public String collection;
	
	@Value("${front-end-url-forgot-password}")
    public String forgotPasswordUrl;

	@Value("${document-path}")
	public String document;

	@Value("${assessment-path}")
	public String assessment;

	@Value("${report-path}")
	public String report;

	@Value("${orc-path}")
	public String orcPath;

	@Value("${form-download-path}")
	public String formDownloadPath;

	@Value("${openproof-path}")
	public String openProof;

	@Value("${image-path}")
	public String imagePath;

	@Value("${default-path}")
	public String defaultPath;

	@Autowired
    QuartzService quartzService;
	
	public void init() {
		
		log.info("Bean started...");
		ConstantDefine.FILE_PATH.EXHIBITION = exhibition;
		ConstantDefine.FILE_PATH.COLLECTION = collection;
		ConstantDefine.FILE_PATH.DOCUMENT = document;
		ConstantDefine.FILE_PATH.ASSESSMENT = assessment;
		ConstantDefine.FILE_PATH.REPORT = report;
		ConstantDefine.FILE_PATH.ORC = orcPath;
		ConstantDefine.FILE_PATH.OPEN_PROOF = openProof;
		ConstantDefine.FILE_PATH.IMAGE_PATH = imagePath;
		ConstantDefine.FILE_PATH.DEFAULT_PATH = defaultPath;
		ConstantDefine.FILE_PATH.FORM_DOWNLOAD = formDownloadPath;
		ConstantDefine.FRONTEND_PATH.RESET_PASSWORD_URL=forgotPasswordUrl;

		String crontab = "0 5 3 * * ?";
		quartzService.deleteJob("deleteAssessmentTemp", "deleteAssessmentTempGroup");
		quartzService.addJob(QuartzJobDeleteAssessmentTemp.class, "deleteAssessmentTemp", "deleteAssessmentTempGroup", crontab, true, null);
	}
	
}
