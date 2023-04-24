package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.entities.DataSource;
import com.ecommerce.core.entities.Form;
import com.ecommerce.core.entities.StatisticalReport;
import com.ecommerce.core.entities.Unit;
import com.ecommerce.core.service.DataSourceService;
import com.ecommerce.core.service.FormService;
import com.ecommerce.core.service.StatisticalReportService;
import com.ecommerce.core.util.FormExport;
import com.ecommerce.core.util.StatisticalExcelExporter;
import com.ecommerce.core.dto.DataLabelDTO;
import com.ecommerce.core.dto.PagingResponse;
import com.ecommerce.core.dto.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("statistical")
@Slf4j
public class StatisticalController extends BaseController{

    private final String START_LOG = "Statistical {}";

    private final String END_LOG = "Statistical {} finished in: {}";

    @Autowired
    StatisticalReportService service;

    @Autowired
    FormService formService;

    @Autowired
    DataSourceService dataSourceService;

    @GetMapping()
    public ResponseModel doSearch(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "sort", required = false) boolean sortDesc,
                                  @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int perPage,
                                  HttpServletRequest request){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doSearch";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            Pageable paging = PageRequest.of(currentPage, perPage);
            Page<StatisticalReport> pageResult = null;
            if (keyword.trim().equals("")) {
                keyword = null;
            } else {
                keyword = keyword.trim().toLowerCase();
            }
            pageResult = service.doSearch(keyword, paging);
            if (pageResult == null || pageResult.isEmpty()){
                responseModel.setErrorMessages("Không tìm thấy báo cáo thống kê!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            PagingResponse<StatisticalReport> result = new PagingResponse<>();
            result.setTotal(pageResult.getTotalElements());
            result.setItems(pageResult.getContent());

            responseModel.setContent(result);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping()
    public ResponseModel doCreate(@RequestParam(value = "id", required = false) Integer id,
                                  @RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "nameEn", required = false) String nameEn,
                                  @RequestParam(value = "isNo", required = false) boolean isNo,
                                  @RequestParam(value = "formId", required = false) Integer formId,
                                  @RequestParam(value = "items", required = false) String items, HttpServletRequest request){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doCreate";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            StatisticalReport entity = new StatisticalReport();
            if (id != null){
                entity = service.retrieve(id);
                entity.setUpdatedBy(getUserFromToken(request));
            } else {
                entity.setCreatedBy(getUserFromToken(request));
            }
            entity.setId(id);
            entity.setName(name);
            entity.setNameEn(nameEn);
            entity.setIsNo(isNo);
            entity.setFormId(formId);
            entity.setItems(items);
            service.create(entity);

            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("{id}")
    public ResponseModel doRetrieve(@PathVariable Integer id){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doRetrieve";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            StatisticalReport entity = service.retrieve(id);
            if (entity == null){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            responseModel.setContent(entity);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("form/{id}")
    public ResponseModel doRetrieveByForm(@PathVariable Integer id){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doRetrieveByForm";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            StatisticalReport entity = service.findByFormIdAndStatus(id);
            if (entity == null){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            responseModel.setContent(entity);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("label")
    public ResponseModel doSearchLabel(@RequestParam List<Integer> listId){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doSearchLabel";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            List<DataSource> label = dataSourceService.getLabel(listId);
            if (label.size() == 0){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            responseModel.setContent(label);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("label/{id}")
    public ResponseModel doGetLabelByKey(@PathVariable Integer id){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doSearchLabel";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            List<DataLabelDTO> label = dataSourceService.getLabelByKey(id);
            if (label.size() == 0){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }

            responseModel.setContent(label);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("{id}")
    public ResponseModel doDelete(@PathVariable Integer id){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doDelete";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            service.delete(id);
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @DeleteMapping("form/{id}")
    public ResponseModel doDeleteByForm(@PathVariable Integer id){
        StopWatch sw = new StopWatch();
        sw.start();
        final String action = "doDeleteByForm";
        log.info(START_LOG, action);
        ResponseModel responseModel = new ResponseModel();
        try {
            StatisticalReport entity = service.findByFormIdAndStatus(id);
            if (entity == null){
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
                return responseModel;
            }
            service.delete(entity.getId());
            responseModel.setStatusCode(HttpStatus.SC_OK);
            responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @GetMapping("export/{id}")
    public void exportStatistical(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        StatisticalReport entity = service.retrieve(id);
//        StatisticalExcelExport export = new StatisticalExcelExport(service, formService, dataSourceService);
        StatisticalExcelExporter export = new StatisticalExcelExporter(service, formService, dataSourceService);
        final JSONArray listItems = (JSONArray) JSONValue.parse(entity.getItems());
        List<Unit> units = new ArrayList<>();
        Integer key = 0;
        List<Integer> listKey = new ArrayList<>();
        for (final Object o: listItems){
            final JSONObject item = (JSONObject) o;
            String itemRadio = item.get((Object) "itemRadio").toString();
            if (itemRadio.equalsIgnoreCase("CSDL")){
                final JSONArray itemsDb = (JSONArray) JSONValue.parse(item.get((Object) "itemDb").toString());
                if (itemsDb != null){
                    for (final Object d:itemsDb){
                        final JSONObject db =(JSONObject) d;
                        String itemDbName = db.get((Object) "itemDbName") == null ? null : db.get((Object) "itemDbName").toString();
                        if (itemDbName != null){
                            Form byId = formService.retrieve(Integer.parseInt(itemDbName));
//                            key = byId.getFormKey();
                            if (!listKey.contains(byId.getFormKey())){
                                listKey.add(byId.getFormKey());
                            }
                            if (byId != null && byId.getUnits().size() > 0){
                                for (Unit u: byId.getUnits()){
                                    if (!units.contains(u)){
                                        units.add(u);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (entity.getItems() != null){
            export.export(entity.getName(), entity.getItems(), entity.getIsNo(), units, listKey, response);
        }
    }

    @GetMapping("export/form/{id}")
    public void exportForm(@PathVariable Integer id, HttpServletResponse response) throws IOException, InvalidFormatException {
        FormExport export = new FormExport(formService, dataSourceService);
        StatisticalReport entity = service.retrieve(id);

        final JSONArray listItems = (JSONArray) JSONValue.parse(entity.getItems());
        List<Unit> units = new ArrayList<>();
        for (final Object o: listItems){
            final JSONObject item = (JSONObject) o;
            String itemRadio = item.get((Object) "itemRadio").toString();
            if (itemRadio.equalsIgnoreCase("CSDL")){
                final JSONArray itemsDb = (JSONArray) JSONValue.parse(item.get((Object) "itemDb").toString());
                if (itemsDb != null){
                    for (final Object d:itemsDb){
                        final JSONObject db =(JSONObject) d;
                        String itemDbName = db.get((Object) "itemDbName") == null ? null : db.get((Object) "itemDbName").toString();
                        if (itemDbName != null){
                            export.export(Integer.parseInt(itemDbName), response);
                        }
                    }
                }
            }
        }
    }
}
