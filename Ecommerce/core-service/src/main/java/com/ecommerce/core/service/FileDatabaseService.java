package com.ecommerce.core.service;

import com.ecommerce.core.entities.FileDatabase;

public interface FileDatabaseService extends IRootService<FileDatabase> {
    FileDatabase findByFormKeyAndUnitIdAndFormId(Integer formKey, Integer unitId, Integer formId);
}
