package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ConstantDefine;
import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ColumnDTO;
import com.ecommerce.core.dto.GenCodeDTO;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.capitalize;

@RestController
@RequestMapping("gen-code")
@Slf4j
public class GenCodeController extends BaseController {
	private final String START_LOG = "UserInfo {}";
	private final String END_LOG = "UserInfo {} finished in: {}";

	@PostMapping()
	public ResponseModel doCreate(@RequestBody UserInfo entity) {
		final String action = "doCreate";
		StopWatch sw = new StopWatch();
		log.info(START_LOG, action);

		try {
			GenCodeDTO dto = new GenCodeDTO();
			dto.setTableName("user_BCD_eft");
			ColumnDTO cdto = new ColumnDTO();
			cdto.setColumnName("ID");
			cdto.setIsId(true);
			cdto.setType("INT");
			List<ColumnDTO> lsst = new ArrayList<ColumnDTO>();
			lsst.add(cdto);
			ColumnDTO cdto1 = new ColumnDTO();
			cdto1.setColumnName("username");
			cdto1.setIsId(false);
			cdto1.setType("VARCHAR");
			lsst.add(cdto1);
			ColumnDTO cdto2 = new ColumnDTO();
			cdto2.setColumnName("phone_number");
			cdto2.setIsId(false);
			cdto2.setType("VARCHAR");
			lsst.add(cdto2);
			ColumnDTO cdto3 = new ColumnDTO();
			cdto3.setColumnName("test_date");
			cdto3.setIsId(false);
			cdto3.setType("DATETIME");
			lsst.add(cdto3);
			dto.setColumns(lsst);
			genEntity(dto);
			genRepository(dto);
			genService(dto);
			genServiceImpl(dto);
			genController(dto);

			ResponseModel responseModel = new ResponseModel();
			responseModel.setStatusCode(HttpStatus.SC_OK);
			responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
			return responseModel;
		} catch (Exception e) {
			throw handleException(e);
		} finally {
			log.info(END_LOG, action, sw.getTotalTimeSeconds());
		}
	}

	private void genEntity(GenCodeDTO dto) {
		String className = convertName(dto.getTableName().toLowerCase(), true);
		Boolean hasDateTime = false;
		try {
			File fout = new File("D:\\New NEO-Themes\\neo-themes\\core-service\\src\\main\\java\\com\\ecommerce\\core\\entities\\" + className + ".java");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("package com.ecommerce.core.entities;");
			bw.newLine();
			bw.newLine();
			for (int i = 0; i < dto.getColumns().size(); i++) {
				ColumnDTO col = dto.getColumns().get(i);
				if(col.getType().equals(ConstantDefine.DATA_TYPE.DATETIME)) {
					hasDateTime = true;
					break;
				}
			}
			if(hasDateTime) {
				bw.write("import java.time.LocalDateTime;");
				bw.newLine();
				bw.newLine();
			}

			bw.write("import javax.persistence.Column;");
			bw.newLine();
			bw.write("import javax.persistence.Entity;");
			bw.newLine();
			bw.write("import javax.persistence.GeneratedValue;");
			bw.newLine();
			bw.write("import javax.persistence.GenerationType;");
			bw.newLine();
			bw.write("import javax.persistence.Id;");
			bw.newLine();
			bw.write("import javax.persistence.Table;");
			bw.newLine();
			bw.newLine();
			bw.write("import lombok.AllArgsConstructor;");
			bw.newLine();
			bw.write("import lombok.Data;");
			bw.newLine();
			bw.write("import lombok.NoArgsConstructor;");
			bw.newLine();
			bw.write("import lombok.experimental.FieldNameConstants;");
			bw.newLine();
			bw.newLine();
			bw.write("@Entity");
			bw.newLine();
			bw.write("@Table(name = \"" + dto.getTableName() + "\")");
			bw.newLine();
			bw.write("@FieldNameConstants");
			bw.newLine();
			bw.write("@Data");
			bw.newLine();
			bw.write("@NoArgsConstructor");
			bw.newLine();
			bw.write("@AllArgsConstructor");
			bw.newLine();
			bw.newLine();
			bw.write("public class " + className + " extends BaseEntity{");
			for (int i = 0; i < dto.getColumns().size(); i++) {
				ColumnDTO col = dto.getColumns().get(i);
				if(col.getIsId()) {
					bw.newLine();
					bw.write("\t@Id");
					bw.newLine();
					bw.write("\t@GeneratedValue(strategy = GenerationType.IDENTITY)");
					bw.newLine();
					bw.write("\t@Column(name = \"" + col.getColumnName().toLowerCase() +"\")");
					bw.newLine();
					bw.write("\tprivate Integer " + convertName(col.getColumnName().toLowerCase(), false) + ";");
				}else {
					bw.newLine();
					bw.write("\t@Column(name = \"" + col.getColumnName().toLowerCase() +"\")");
					bw.newLine();
					String type = "";
					if(col.getType().equals(ConstantDefine.DATA_TYPE.INT)) {
						type = "Integer";
					} else if(col.getType().equals(ConstantDefine.DATA_TYPE.VARCHAR)) {
						type = "String";
					} else if(col.getType().equals(ConstantDefine.DATA_TYPE.DATETIME)) {
						type = "LocalDateTime";
					}
					bw.write("\tprivate " + type + " " + convertName(col.getColumnName().toLowerCase(), false) + ";");
				}
			}
			bw.newLine();
			bw.write("}");

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	private void genRepository(GenCodeDTO dto) {
		String entityName = convertName(dto.getTableName().toLowerCase(), true);
		String className = convertName(dto.getTableName().toLowerCase(), true) + "Repository";
		try {
			File fout = new File("D:\\New NEO-Themes\\neo-themes\\core-service\\src\\main\\java\\com\\ecommerce\\core\\repositories\\" + className + ".java");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("package com.ecommerce.core.repositories;");
			bw.newLine();
			bw.newLine();
			bw.write("import org.springframework.data.jpa.repository.JpaRepository;");
			bw.newLine();
			bw.newLine();
			bw.write("import com.ecommerce.core.entities." + entityName + ";");
			bw.newLine();
			bw.newLine();
			bw.write("public interface " + className + " extends JpaRepository<" + entityName + ", Integer> {");
			bw.newLine();
			bw.write("}");

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void genService(GenCodeDTO dto) {
		String entityName = convertName(dto.getTableName().toLowerCase(), true);
		String className = convertName(dto.getTableName().toLowerCase(), true) + "Service";
		try {
			File fout = new File("D:\\New NEO-Themes\\neo-themes\\core-service\\src\\main\\java\\com\\ecommerce\\core\\service\\" + className + ".java");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("package com.ecommerce.core.service;");
			bw.newLine();
			bw.newLine();
			bw.write("import org.springframework.stereotype.Service;");
			bw.newLine();
			bw.newLine();
			bw.write("import com.ecommerce.core.entities." + entityName + ";");
			bw.newLine();
			bw.newLine();
			bw.write("@Service");
			bw.newLine();
			bw.write("public interface " + className + " extends IRootService<" + entityName + ">{");
			bw.newLine();
			bw.write("}");

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void genServiceImpl(GenCodeDTO dto) {
		String entityName = convertName(dto.getTableName().toLowerCase(), true);
		String className = convertName(dto.getTableName().toLowerCase(), true) + "ServiceImpl";
		try {
			File fout = new File("D:\\New NEO-Themes\\neo-themes\\core-service\\src\\main\\java\\com\\ecommerce\\core\\service\\impl\\" + className + ".java");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("package com.ecommerce.core.service.impl;");
			bw.newLine();
			bw.newLine();
			bw.write("import java.util.Optional;");
			bw.newLine();
			bw.newLine();
			bw.write("import org.springframework.beans.factory.annotation.Autowired;");
			bw.newLine();
			bw.write("import org.springframework.stereotype.Service;");
			bw.newLine();
			bw.newLine();
			bw.write("import com.ecommerce.core.entities." + entityName + ";");
			bw.newLine();
			bw.write("import com.ecommerce.core.repositories." + entityName + "Repository;");
			bw.newLine();
			bw.write("import com.ecommerce.core.service." + entityName + "Service;");
			bw.newLine();
			bw.newLine();
			bw.write("@Service");
			bw.newLine();
			bw.write("public class " + className + " implements " + entityName + "Service {");
			bw.newLine();
			bw.newLine();
			bw.write("\t@Autowired");
			bw.newLine();
			bw.write("\t" + entityName + "Repository repo;");
			bw.newLine();
			bw.newLine();
			bw.write("\t@Override");
			bw.newLine();
			bw.write("\tpublic " + entityName + " create(" + entityName + " entity) {");
			bw.newLine();
			bw.write("\t\treturn repo.save(entity);");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			bw.write("\t@Override");
			bw.newLine();
			bw.write("\tpublic " + entityName + " retrieve(Integer id) {");
			bw.newLine();
			bw.write("\t\tOptional<" + entityName + "> entity = repo.findById(id);");
			bw.newLine();
			bw.write("\t\tif (!entity.isPresent()) {");
			bw.newLine();
			bw.write("\t\t\treturn null;");
			bw.newLine();
			bw.write("\t\t}");
			bw.newLine();
			bw.write("\t\treturn entity.get();");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			bw.write("\t@Override");
			bw.newLine();
			bw.write("\tpublic void update(" + entityName + " entity, Integer id) {");
			bw.newLine();
			bw.write("\t\trepo.save(entity);");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			bw.write("\t@Override");
			bw.newLine();
			bw.write("\tpublic void delete(Integer id) {");
			bw.newLine();
			bw.write("\t\trepo.deleteById(id);");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.write("}");

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void genController(GenCodeDTO dto) {
		String entityName = convertName(dto.getTableName().toLowerCase(), true);
		String className = convertName(dto.getTableName().toLowerCase(), true) + "Controller";
		Boolean hasCreatedBy = false;
		Boolean hasUpdatedBy = true;
		String idColumn = "";
		try {
			//has created_by
			for (int i = 0; i < dto.getColumns().size(); i++) {
				if(dto.getColumns().get(i).equals("created_by")) {
					hasCreatedBy = true;
				}
				if(dto.getColumns().get(i).equals("updated_by")) {
					hasUpdatedBy = true;
				}
				if(dto.getColumns().get(i).getIsId()) {
					idColumn = dto.getColumns().get(i).getColumnName();
				}
			}
			File fout = new File("D:\\New NEO-Themes\\neo-themes\\core-service\\src\\main\\java\\com\\ecommerce\\core\\controllers\\" + className + ".java");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("package com.ecommerce.core.controllers;");
			bw.newLine();
			bw.newLine();
			bw.write("import javax.servlet.http.HttpServletRequest;");
			bw.newLine();
			bw.newLine();
			bw.write("import org.apache.http.HttpStatus;");
			bw.newLine();
			bw.write("import org.springframework.beans.factory.annotation.Autowired;");
			bw.newLine();
			bw.write("import org.springframework.util.StopWatch;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.DeleteMapping;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.GetMapping;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.PathVariable;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.PostMapping;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.PutMapping;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.RequestBody;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.RequestParam;");
			bw.newLine();
			bw.write("import org.springframework.web.bind.annotation.RestController;");
			bw.newLine();
			bw.newLine();
			bw.write("import com.ecommerce.core.constants.ResponseFontendDefine;");
			bw.newLine();
			bw.write("import com.ecommerce.core.dto.ResponseModel;");
			bw.newLine();
			bw.write("import com.ecommerce.core.entities." + entityName + ";");
			bw.newLine();
			bw.write("import com.ecommerce.core.service." + entityName + "Service;");
			bw.newLine();
			bw.newLine();
			bw.write("import lombok.extern.slf4j.Slf4j;");
			bw.newLine();
			bw.newLine();
			bw.write("@RestController");
			bw.newLine();
			bw.write("@RequestMapping(\"" + dto.getTableName() + "\")");
			bw.newLine();
			bw.write("@Slf4j");
			bw.newLine();
			bw.newLine();
			bw.write("public class " + className + " extends BaseController {");
			bw.newLine();
			bw.write("\tprivate final String START_LOG = \"" + entityName + " {}\";");
			bw.newLine();
			bw.write("\tprivate final String END_LOG = \"" + entityName + " {} finished in: {}\";");
			bw.newLine();
			bw.newLine();
			bw.write("\t@Autowired");
			bw.newLine();
			bw.write("\t" + entityName + "Service service;");
			bw.newLine();
			bw.newLine();
			//Retrieve
			bw.write("\t@GetMapping(\"{id}\")");
			bw.newLine();
			bw.write("\tpublic ResponseModel doRetrieve(@PathVariable Integer id) {");
			bw.newLine();
			bw.write("\t\tfinal String action = \"doRetrieve\";");
			bw.newLine();
			bw.write("\t\tStopWatch sw = new StopWatch();");
			bw.newLine();
			bw.write("\t\tlog.info(START_LOG, action);");
			bw.newLine();
			bw.write("\t\ttry {");
			bw.newLine();
			bw.write("\t\t\t" + entityName + " entity = service.retrieve(id);");
			bw.newLine();
			bw.write("\t\t\tif (entity == null) {");
			bw.newLine();
			bw.write("\t\t\t\tResponseModel responseModel = new ResponseModel();");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setErrorMessages(\"Không tìm thấy kết quả.\");");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setStatusCode(HttpStatus.SC_OK);");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);");
			bw.newLine();
			bw.write("\t\t\t\treturn responseModel;");
			bw.newLine();
			bw.write("\t\t\t}");
			bw.newLine();
			bw.newLine();
			bw.write("\t\t\tResponseModel responseModel = new ResponseModel();");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setContent(entity);");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setStatusCode(HttpStatus.SC_OK);");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);");
			bw.newLine();
			bw.write("\t\t\treturn responseModel;");
			bw.newLine();
			bw.write("\t\t} catch (Exception e) {");
			bw.newLine();
			bw.write("\t\t\tthrow handleException(e);");
			bw.newLine();
			bw.write("\t\t} finally {");
			bw.newLine();
			bw.write("\t\t\tlog.info(END_LOG, action, sw.getTotalTimeSeconds());");
			bw.newLine();
			bw.write("\t\t}");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			//Create
			bw.write("\t@PostMapping()");
			bw.newLine();
			bw.write("\tpublic ResponseModel doCreate(@RequestBody " + entityName + " entity, HttpServletRequest request) {");
			bw.newLine();
			bw.write("\t\tfinal String action = \"doCreate\";");
			bw.newLine();
			bw.write("\t\tStopWatch sw = new StopWatch();");
			bw.newLine();
			bw.write("\t\tlog.info(START_LOG, action);");
			bw.newLine();
			bw.newLine();
			bw.write("\t\ttry {");
			if(hasCreatedBy) {
				bw.newLine();
				bw.write("\t\t\tentity.setCreatedBy(getUserFromToken(request);");
			}
			bw.newLine();
			bw.write("\t\t\tservice.create(entity);");
			bw.newLine();
			bw.write("\t\t\tResponseModel responseModel = new ResponseModel();");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setStatusCode(HttpStatus.SC_OK);");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);");
			bw.newLine();
			bw.write("\t\t\treturn responseModel;");
			bw.newLine();
			bw.write("\t\t} catch (Exception e) {");
			bw.newLine();
			bw.write("\t\t\tthrow handleException(e);");
			bw.newLine();
			bw.write("\t\t} finally {");
			bw.newLine();
			bw.write("\t\t\tlog.info(END_LOG, action, sw.getTotalTimeSeconds());");
			bw.newLine();
			bw.write("\t\t}");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			//Update
			bw.write("\t@PutMapping()");
			bw.newLine();
			bw.write("\tpublic ResponseModel doUpdate(@RequestBody " + entityName + " entity, HttpServletRequest request) {");
			bw.newLine();
			bw.write("\t\tfinal String action = \"doUpdate\";");
			bw.newLine();
			bw.write("\t\tStopWatch sw = new StopWatch();");
			bw.newLine();
			bw.write("\t\tlog.info(START_LOG, action);");
			bw.newLine();
			bw.newLine();
			bw.write("\t\ttry {");
			bw.newLine();
			bw.write("\t\t\t" + entityName + " currEntity = service.retrieve(entity.get" + convertName(idColumn.toLowerCase(), true) + "());");
			bw.newLine();
			bw.write("\t\t\tif (currEntity == null) {");
			bw.newLine();
			bw.write("\t\t\t\tResponseModel responseModel = new ResponseModel();");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setErrorMessages(\"Không tìm thấy kết quả.\");");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setStatusCode(HttpStatus.SC_OK);");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);");
			bw.newLine();
			bw.write("\t\t\t\treturn responseModel;");
			bw.newLine();
			bw.write("\t\t\t}");
			bw.newLine();
			bw.newLine();
			if(hasUpdatedBy) {
				bw.write("\t\t\tentity.setUpdatedBy(getUserFromToken(request));");
				bw.newLine();
			}
			bw.write("\t\t\tservice.update(entity, entity.get" + convertName(idColumn.toLowerCase(), true) + "());");
			bw.newLine();
			bw.write("\t\t\tResponseModel responseModel = new ResponseModel();");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setContent(entity);");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setStatusCode(HttpStatus.SC_OK);");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);");
			bw.newLine();
			bw.write("\t\t\treturn responseModel;");
			bw.newLine();
			bw.write("\t\t} catch (Exception e) {");
			bw.newLine();
			bw.write("\t\t\tthrow handleException(e);");
			bw.newLine();
			bw.write("\t\t} finally {");
			bw.newLine();
			bw.write("\t\t\tlog.info(END_LOG, action, sw.getTotalTimeSeconds());");
			bw.newLine();
			bw.write("\t\t}");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			
			//Delete
			bw.write("\t@DeleteMapping(\"{id}\")");
			bw.newLine();
			bw.write("\tpublic ResponseModel doDelete(@PathVariable Integer id) {");
			bw.newLine();
			bw.write("\t\tfinal String action = \"doDelete\";");
			bw.newLine();
			bw.write("\t\tStopWatch sw = new StopWatch();");
			bw.newLine();
			bw.write("\t\tlog.info(START_LOG, action);");
			bw.newLine();
			bw.newLine();
			bw.write("\t\ttry {");
			bw.newLine();
			bw.write("\t\t\t" + entityName + " entity = service.retrieve(id);");
			bw.newLine();
			bw.write("\t\t\tif (entity == null) {");
			bw.newLine();
			bw.write("\t\t\t\tResponseModel responseModel = new ResponseModel();");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setErrorMessages(\"Không tìm thấy kết quả.\");");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setStatusCode(HttpStatus.SC_OK);");
			bw.newLine();
			bw.write("\t\t\t\tresponseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);");
			bw.newLine();
			bw.write("\t\t\t\treturn responseModel;");
			bw.newLine();
			bw.write("\t\t\t}");
			bw.newLine();
			bw.newLine();
			bw.write("\t\t\tservice.delete(entity.get" + convertName(idColumn.toLowerCase(), true) + "());");
			bw.newLine();
			bw.write("\t\t\tResponseModel responseModel = new ResponseModel();");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setContent(entity);");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setStatusCode(HttpStatus.SC_OK);");
			bw.newLine();
			bw.write("\t\t\tresponseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);");
			bw.newLine();
			bw.write("\t\t\treturn responseModel;");
			bw.newLine();
			bw.write("\t\t} catch (Exception e) {");
			bw.newLine();
			bw.write("\t\t\tthrow handleException(e);");
			bw.newLine();
			bw.write("\t\t} finally {");
			bw.newLine();
			bw.write("\t\t\tlog.info(END_LOG, action, sw.getTotalTimeSeconds());");
			bw.newLine();
			bw.write("\t\t}");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String convertName(String name, boolean isClass) {
		String[] arrtables = name.split("_");
		String entityName = "";
		for (int i = 0; i < arrtables.length; i++) {
			if (isClass) {
				entityName += capitalize(arrtables[i]);
			} else {
				if (i == 0) {
					entityName += arrtables[i];
				} else {
					entityName += capitalize(arrtables[i]);
				}
			}

		}
		return entityName;
	}
}
