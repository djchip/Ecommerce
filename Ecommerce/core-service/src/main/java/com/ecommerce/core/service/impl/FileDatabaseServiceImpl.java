package com.ecommerce.core.service.impl;

import com.ecommerce.core.entities.FileDatabase;
import com.ecommerce.core.entities.Form;
import com.ecommerce.core.repositories.FileDatabaseRepository;
import com.ecommerce.core.service.FileDatabaseService;
import com.ecommerce.core.repositories.FormRepository;
import com.ecommerce.core.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileDatabaseServiceImpl implements FileDatabaseService {

    @Autowired
    private FileDatabaseRepository repository;

    @Autowired
    private FormService formService;

    @Autowired
    private FormRepository formRepository;

    @Override
    public FileDatabase create(FileDatabase entity) {
        return repository.save(entity);
    }

    @Override
    public FileDatabase retrieve(Integer id) {
        return null;
    }

    @Override
    public void update(FileDatabase entity, Integer id) {
        FileDatabase oldFileDatabase = repository.findByFormKeyAndUnitId(entity.getFormKey(), entity.getUnitId());
        oldFileDatabase.setPathFile(entity.getPathFile());
        oldFileDatabase.setStatus(1); // da duoc upload CSDL
        Form form = formService.findByFormKey(entity.getFormKey());
        form.setStatus(3);

        boolean isAllUnitUploadDb = true;
        List<FileDatabase> listFileDbByFormKey = repository.findByFormKey(form.getFormKey());
        for(FileDatabase fileDatabase : listFileDbByFormKey){
            if (fileDatabase.getStatus() == 0) {
                isAllUnitUploadDb = false;
                break;
            }
        }
        if (isAllUnitUploadDb){
            form.setStatus(4);
        }
        formRepository.save(form);
        repository.save(oldFileDatabase);
    }

    @Override
    public void delete(Integer id) throws Exception {

    }

    @Override
    public FileDatabase findByFormKeyAndUnitIdAndFormId(Integer formKey, Integer unitId, Integer formId) {
        return repository.findByFormKeyAndUnitIdAndFormId(formKey, unitId, formId);
    }
}
