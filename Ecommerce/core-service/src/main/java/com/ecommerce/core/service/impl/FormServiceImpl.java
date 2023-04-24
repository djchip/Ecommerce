package com.ecommerce.core.service.impl;

import com.ecommerce.core.constants.TypeEnum;
import com.ecommerce.core.entities.FileDatabase;
import com.ecommerce.core.entities.Form;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.exceptions.DetectExcelException;
import com.ecommerce.core.repositories.DataSourceRepository;
import com.ecommerce.core.repositories.FileDatabaseRepository;
import com.ecommerce.core.repositories.FormRepository;
import com.ecommerce.core.service.*;
import com.ecommerce.core.dto.DetailFormDTO;
import com.ecommerce.core.dto.FormCopyDTO;
import com.ecommerce.core.dto.FormDTO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FormServiceImpl implements FormService {
    private static final int CAN_DELETED = 0;
    private static final Integer DELETED = 1;
    private static final String TABLE_NAME = "Form";
    private static final int UPLOAD = 0;
    private static final int CREATED = 0; // Mới thêm CSDL
    private static final int DIVIDED_INTO_UNIT = 1; // Đã phân về đơn vị
    private static final int UPDATE = 2; // Đã cập nhật
    private static final int UPLOADING = 3; // Các đơn vị đang upload CSDL
    private static final int UPLOADED = 4; // Các đơn vị đã hoàn thành upload CSDL
    @Value("${form-path}")
    private String PATH_FILE;
    @Autowired
    FormRepository formRepository;
    @Autowired
    UndoLogService undoLogService;
    @Autowired
    FileDatabaseService databaseService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    DataSourceRepository dataSourceRepository;
    @Autowired
    FileDatabaseRepository fileDatabaseRepository;

    @Autowired
    DataSourceService dataSourceService;

    @Override
    public List<Form> listAll() {
        return formRepository.findByExport();
    }

    @Override
    public Page<Form> doSearch(String keyword, Integer yearOfApplication, String uploadTime, Pageable pageable) throws ParseException {
        Date date = null;
        if (!uploadTime.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = formatter.parse(uploadTime);
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
        }
        System.out.println(date + " HHH");
        return formRepository.doSearch(keyword, yearOfApplication, date, pageable);
    }

    @Override
    public Page<FormDTO> doSearchDataBase(String keyword, Integer yearOfApplication, String uploadTime, Integer unitId, Pageable pageable) throws ParseException {
        Date date = null;
        if (!uploadTime.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = formatter.parse(uploadTime);
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
        }
        return formRepository.doSearch(keyword, yearOfApplication, date, unitId, pageable);
    }

    @Override
    public Page<Form> doSearchByUnit(Integer unitId, Pageable pageable) {
        return formRepository.doSearchByUnit(unitId, pageable);
    }

    @Transactional
    @Override
    public Form create(Form entity) {
        entity.setDeleted(CAN_DELETED);
        entity.setCreateDate(LocalDateTime.now());
        entity.setStatus(DIVIDED_INTO_UNIT);
        entity.getUploadTime().setHours(0);
        entity.getUploadTime().setMinutes(0);
        entity.getUploadTime().setSeconds(0);
        for (Unit unit : entity.getUnits()) {
            FileDatabase fileDatabase = new FileDatabase();
            fileDatabase.setFormKey(entity.getFormKey());
            fileDatabase.setUnitId(unit.getId());
            fileDatabase.setStatus(UPLOAD);
            databaseService.create(fileDatabase);
        }
        return formRepository.save(entity);
    }

    @Override
    public Form doCopyForm(FormCopyDTO dto, String usernameFormRequest, HttpServletRequest request) throws IOException, DetectExcelException {
        Optional<Form> optional = formRepository.findById(dto.getId());
        Form oldForm = null;
        if (optional.isPresent()) {
            oldForm = optional.get();
        }
        Form form = new Form();
        form.setName(dto.getName());
        form.setNameEn(dto.getNameEn());
        form.setYearOfApplication(dto.getYearOfApplication());
        form.setUnits(dto.getUnits());
        Date uploadDate = dto.getUploadTime();
        uploadDate.setHours(0);
        uploadDate.setMinutes(0);
        uploadDate.setSeconds(0);
        form.setUploadTime(uploadDate);
        form.setFileName(oldForm.getFileName());
        form.setCreateBy(usernameFormRequest);
        form.setCreateDate(LocalDateTime.now());
        form.setDeleted(CAN_DELETED);
        form.setStatus(DIVIDED_INTO_UNIT);
        form.setNumTitle(oldForm.getNumTitle());
        form.setStartTitle(oldForm.getStartTitle());
        Integer formKey = getMaxFormKey() + 1;
        form.setFormKey(formKey);
        form.setPathFile(PATH_FILE + "/" + formKey + "/" + dto.getName() + "/" + oldForm.getFileName());


        String oldPathFile = oldForm.getPathFile();//Get old path file
        File file = new File(oldPathFile);
        // Tạo một đối tượng FileItem từ đối tượng File
        FileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length(), file.getParentFile());
        // Ghi nội dung của tệp vào đối tượng FileItem
        fileItem.getOutputStream().write(FileUtils.readFileToByteArray(file));
        // Tạo đối tượng MultipartFile từ FileItem
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

        dataSourceService.importDataSource(multipartFile, usernameFormRequest, oldForm.getNumTitle(), true, dto.getName(), dto.getYearOfApplication(), formKey, oldForm.getStartTitle(), request);

        for (Unit unit : dto.getUnits()) {
            FileDatabase fileDatabase = new FileDatabase();
            fileDatabase.setFormKey(formKey);
            fileDatabase.setUnitId(unit.getId());
            fileDatabase.setStatus(UPLOAD);
            databaseService.create(fileDatabase);
        }
        return formRepository.save(form);
    }

    @Override
    public void copyMulti(List<Form> forms, String user, HttpServletRequest request) throws IOException, DetectExcelException {
        for (Form form : forms) {
            Form newForm = new Form();
            newForm.setName(form.getName());
            newForm.setNameEn(form.getNameEn());
            newForm.setYearOfApplication(form.getYearOfApplication());
            newForm.setUnits(form.getUnits());
            Date uploadDate = form.getUploadTime();
            uploadDate.setHours(0);
            uploadDate.setMinutes(0);
            uploadDate.setSeconds(0);
            newForm.setUploadTime(uploadDate);
            newForm.setFileName(form.getFileName());
            newForm.setCreateBy(user);
            newForm.setCreateDate(LocalDateTime.now());
            newForm.setDeleted(CAN_DELETED);
            newForm.setStatus(DIVIDED_INTO_UNIT);
            newForm.setNumTitle(form.getNumTitle());
            Integer formKey = getMaxFormKey() + 1;
            newForm.setFormKey(formKey);
            newForm.setStartTitle(form.getStartTitle());
            newForm.setPathFile(PATH_FILE + "/" + formKey + "/" + form.getName() + "/" + form.getFileName());

            String oldPathFile = form.getPathFile();//Get old path file
            File file = new File(oldPathFile);
            // Tạo một đối tượng FileItem từ đối tượng File
            FileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length(), file.getParentFile());
            // Ghi nội dung của tệp vào đối tượng FileItem
            fileItem.getOutputStream().write(FileUtils.readFileToByteArray(file));
            // Tạo đối tượng MultipartFile từ FileItem
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            dataSourceService.importDataSource(multipartFile, user, form.getNumTitle(), true, form.getName(), form.getYearOfApplication(), formKey, form.getStartTitle(), request);

            for (Unit unit : newForm.getUnits()) {
                FileDatabase fileDatabase = new FileDatabase();
                fileDatabase.setFormKey(formKey);
                fileDatabase.setUnitId(unit.getId());
                fileDatabase.setStatus(UPLOAD);
                databaseService.create(fileDatabase);
            }
            formRepository.save(newForm);
        }
    }

    @Override
    public Form retrieve(Integer id) {
        Optional<Form> entity = formRepository.findById(id);
        return entity.orElse(null);
    }

    @Override
    public DetailFormDTO getDetailForm(Integer id, Integer unitId) {
        Optional<Form> entity = formRepository.findById(id);
        DetailFormDTO formDTO = new DetailFormDTO();
        if (entity.isPresent()) {
            formDTO.setId(id);
            formDTO.setName(entity.get().getName());
            formDTO.setNameEn(entity.get().getNameEn());
            formDTO.setFileName(entity.get().getFileName());
            formDTO.setUploadTime(entity.get().getUploadTime());
            formDTO.setDeleted(entity.get().getDeleted());
            formDTO.setYearOfApplication(entity.get().getYearOfApplication());
            formDTO.setStatus(entity.get().getStatus());
            formDTO.setPathFile(entity.get().getPathFile());
            formDTO.setNumTitle(entity.get().getNumTitle());
            formDTO.setFormKey(entity.get().getFormKey());
            formDTO.setStartTitle(entity.get().getStartTitle());
            formDTO.setUnits(entity.get().getUnits());
            String pathFileDatabase = "";
            System.out.println("a - " + entity.get().getFormKey());
            System.out.println("a - " + unitId);
            System.out.println("a - " + id);
            FileDatabase fileDatabase = fileDatabaseRepository.findByFormKeyAndUnitIdAndFormId(entity.get().getFormKey(), unitId, id);
            pathFileDatabase = fileDatabase != null ? fileDatabase.getPathFile() : null;
            formDTO.setPathFileDatabase(pathFileDatabase);

            return formDTO;
        }

        return null;
    }


    @Transactional
    @Override
    public void update(Form entity, Integer id) {
        Optional<Form> optional = formRepository.findById(id);
        if (optional.isPresent()) {
            Form newForm = optional.get();
            System.out.println("NEW FORM \n" + newForm);
            newForm.setUpdateDate(LocalDateTime.now());
            newForm.setCreateBy(entity.getUpdateBy());
            newForm.setName(entity.getName());
            newForm.setNameEn(entity.getNameEn());
            newForm.setYearOfApplication(entity.getYearOfApplication());
            Date uploadDate = entity.getUploadTime();
            uploadDate.setHours(0);
            uploadDate.setMinutes(0);
            uploadDate.setSeconds(0);
            newForm.setUploadTime(uploadDate);
            newForm.setFileName(entity.getFileName());
            if (newForm.getStatus() == DIVIDED_INTO_UNIT || newForm.getStatus() == UPDATE) {
                newForm.setStatus(UPDATE);
            }
            newForm.setUnits(entity.getUnits());
            newForm.setNumTitle(entity.getNumTitle());
            newForm.setPathFile(entity.getPathFile());
            newForm.setFormKey(entity.getFormKey());
            formRepository.save(newForm);
            List<FileDatabase> databases = fileDatabaseRepository.findByFormKey(entity.getFormKey());
            List<Integer> listUnitId = new ArrayList<>(databases.size());
            System.out.println("ENTITY " + entity);
            for (FileDatabase fileDatabase : databases) {
                listUnitId.add(fileDatabase.getUnitId());
            }
            for (Unit unit : entity.getUnits()) {
                if (listUnitId.contains(unit.getId())) {
                    listUnitId.remove(unit.getId());
                } else {
                    FileDatabase fileDatabase = new FileDatabase();
                    fileDatabase.setFormKey(entity.getFormKey());
                    fileDatabase.setUnitId(unit.getId());
                    fileDatabase.setStatus(UPLOAD);
                    databaseService.create(fileDatabase);
                }
            }
            System.out.println("LiST UNIT ID " + listUnitId);
            for (Integer idUnit : listUnitId) {
                fileDatabaseRepository.deleteByIdAndFormKey(entity.getFormKey(), idUnit);
            }
        }
    }

    @Transactional
    @Override
    public void updateWithoutFile(Form entity, Integer id, String updateBy) {
        Optional<Form> optional = formRepository.findById(id);
        if (optional.isPresent()) {
            Form newForm = optional.get();
            System.out.println("NEW FORM \n" + newForm);
            newForm.setUpdateDate(LocalDateTime.now());
            newForm.setUpdateBy(updateBy);
            newForm.setName(entity.getName());
            newForm.setNameEn(entity.getNameEn());
            newForm.setYearOfApplication(entity.getYearOfApplication());
            Date uploadDate = entity.getUploadTime();
            uploadDate.setHours(0);
            uploadDate.setMinutes(0);
            uploadDate.setSeconds(0);
            newForm.setUploadTime(uploadDate);
            newForm.setUnits(entity.getUnits());
            if (newForm.getStatus() == DIVIDED_INTO_UNIT || newForm.getStatus() == UPDATE) {
                newForm.setStatus(UPDATE);
            }
            formRepository.save(newForm);
            List<FileDatabase> databases = fileDatabaseRepository.findByFormKey(newForm.getFormKey());
            List<Integer> listUnitId = new ArrayList<>(databases.size());
            System.out.println("ENTITY " + entity);
            for (FileDatabase fileDatabase : databases) {
                listUnitId.add(fileDatabase.getUnitId());
            }
            for (Unit unit : entity.getUnits()) {
                if (listUnitId.contains(unit.getId())) {
                    listUnitId.remove(unit.getId());
                } else {
                    FileDatabase fileDatabase = new FileDatabase();
                    fileDatabase.setFormKey(newForm.getFormKey());
                    fileDatabase.setUnitId(unit.getId());
                    fileDatabase.setStatus(UPLOAD);
                    databaseService.create(fileDatabase);
                }
            }
            System.out.println("LiST UNIT ID " + listUnitId);
            for (Integer idUnit : listUnitId) {
                fileDatabaseRepository.deleteByIdAndFormKey(newForm.getFormKey(), idUnit);
            }
        }
    }

    @Override
    public void delete(Integer id) {
        formRepository.deleteById(id);
    }

    @Override
    public List<Form> save(List<Form> formList) {
        return formRepository.saveAll(formList);
    }

    @Override
    public void save(Form entity) {
        formRepository.save(entity);
    }

    @Override
    public Form findById(int id) {
        return formRepository.findById(id).get();
    }

    @Override
    public Form deleteForm(Integer id) throws Exception {
        Optional<Form> optional = formRepository.findById(id);
        if (optional.isPresent()) {
            Form form = optional.get();
            form.setDeleted(DELETED);
            return formRepository.save(form);
        } else {
            throw new Exception();
        }
    }

    @Transactional
    @Override
    public Boolean deleteMulti(List<Form> forms) throws IOException {
        boolean canDelete = true;
        for (Form form : forms) {
            if (form.getStatus() == UPLOADING || form.getStatus() == UPLOADED) {
                canDelete = false;
                break;
            }
        }
        if (canDelete) {
            for (Form form : forms) {
                formRepository.delete(form);
                dataSourceRepository.deleteByFormKey(form.getFormKey());
                // chua xoa file form FORM
                fileDatabaseRepository.deleteByFormKey(form.getFormKey());
                Files.delete(Paths.get(form.getPathFile()));
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public void deleteCSDL(List<FormDTO> dtos) throws IOException {
        for (FormDTO dto : dtos) {
            // Xoa data o bang DataSource
            dataSourceRepository.deleteByFormKeyAndCreatedUnitAndTypeIsNot(dto.getFormKey(), dto.getUnitId(), TypeEnum.LABEL);
            // Xoá path_file ở bảng file_database
            FileDatabase fileDatabase = fileDatabaseRepository.findByFormKeyAndUnitId(dto.getFormKey(), dto.getUnitId());
            fileDatabase.setStatus(UPLOAD);
            String filePath = fileDatabase.getPathFile();
            fileDatabase.setPathFile(null);
            fileDatabaseRepository.save(fileDatabase);
            List<FileDatabase> fileDatabasesByFormKey = fileDatabaseRepository.findByFormKey(dto.getFormKey());
            List<Integer> listStatusOfFileDatabaseByFormKey = new ArrayList<>();
            for (FileDatabase database : fileDatabasesByFormKey) {
                listStatusOfFileDatabaseByFormKey.add(database.getStatus());
            }
            if (listStatusOfFileDatabaseByFormKey.contains(1)) {
                formRepository.findByFormKey(dto.getFormKey()).setStatus(UPLOADING);
            } else {
                formRepository.findByFormKey(dto.getFormKey()).setStatus(DIVIDED_INTO_UNIT);
            }
            formRepository.save(formRepository.findByFormKey(dto.getFormKey()));
            // xoa file
            if (filePath != null) {
                Files.delete(Paths.get(filePath));
            }
        }
    }

    @Override
    public Form findByFormKey(Integer formKey) {
        return formRepository.findByFormKey(formKey);
    }

    @Override
    public List<Form> listFormUploaded(Integer year) {
        return formRepository.listFormUploaded(year);
    }

    @Override
    public List<Form> totalForm(Integer year) {
        return formRepository.totalForm(year);
    }

    @Override
    public List<Form> listFormNotUploaded(Integer year) {
        return formRepository.listFormNotUploaded(year);
    }

    @Override
    public Integer getMaxFormKey() {
        return formRepository.getMaxFormKey();
    }

    @Transactional
    @Override
    public void doUpdate(Form entity, Integer id, String updateBy) {
        Optional<Form> optional = formRepository.findById(id);
        if (optional.isPresent()) {
            Form newForm = optional.get();
            System.out.println("NEW FORM \n" + newForm);
            newForm.setUpdateDate(LocalDateTime.now());
//            newForm.setCreateBy(entity.getUpdateBy());
            newForm.setName(entity.getName());
            newForm.setUpdateBy(updateBy);
            newForm.setNameEn(entity.getNameEn());
            newForm.setYearOfApplication(entity.getYearOfApplication());
            Date uploadDate = entity.getUploadTime();
            uploadDate.setHours(0);
            uploadDate.setMinutes(0);
            uploadDate.setSeconds(0);
            newForm.setUploadTime(uploadDate);
            newForm.setFileName(entity.getFileName());
//            newForm.setStatus(entity.getStatus());
            newForm.setUnits(entity.getUnits());
            newForm.setNumTitle(entity.getNumTitle());
            newForm.setPathFile(entity.getPathFile());
            newForm.setFormKey(entity.getFormKey());
            newForm.setStartTitle(entity.getStartTitle());
            formRepository.save(newForm);

//            dataSourceRepository.deleteByFormKey(newForm.getFormKey());

            List<FileDatabase> databases = fileDatabaseRepository.findByFormKey(entity.getFormKey());
            List<Integer> listUnitId = new ArrayList<>(databases.size());
            System.out.println("ENTITY " + entity);
            for (FileDatabase fileDatabase : databases) {
                listUnitId.add(fileDatabase.getUnitId());
            }
            for (Unit unit : entity.getUnits()) {
                if (listUnitId.contains(unit.getId())) {
                    listUnitId.remove(unit.getId());
                } else {
                    FileDatabase fileDatabase = new FileDatabase();
                    fileDatabase.setFormKey(newForm.getFormKey());
                    fileDatabase.setUnitId(unit.getId());
                    fileDatabase.setStatus(UPLOAD);
                    databaseService.create(fileDatabase);
                }
            }
            System.out.println("LiST UNIT ID " + listUnitId);
            for (Integer idUnit : listUnitId) {
                fileDatabaseRepository.deleteByIdAndFormKey(entity.getFormKey(), idUnit);
            }
        }
    }

}
