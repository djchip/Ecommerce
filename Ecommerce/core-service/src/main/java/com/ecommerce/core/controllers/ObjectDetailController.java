package com.ecommerce.core.controllers;

import com.ecommerce.core.constants.ResponseFontendDefine;
import com.ecommerce.core.dto.ResponseModel;
import com.ecommerce.core.entities.ObjectDetail;
import com.ecommerce.core.service.ObjectDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("object-detail")
@Slf4j
public class ObjectDetailController extends BaseController{

    private final String START_LOG = "Object Detail {}";

    private final String END_LOG = "Object Detail {} finished in: {}";

    @Autowired
    ObjectDetailService service;

    @GetMapping("{id}")
    public ResponseModel getDetail(@PathVariable("id") Integer id){
        final String action = "doRetrieve";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            List<ObjectDetail> listByObj = service.getListByObj(id);
            ResponseModel responseModel = new ResponseModel();
            if (listByObj.isEmpty()){
                responseModel.setErrorMessages("Không tìm thấy thông tin chi tiết đối tượng!!!");
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_NOT_FOUND);
            } else {
                responseModel.setContent(listByObj);
                responseModel.setStatusCode(HttpStatus.SC_OK);
                responseModel.setCode(ResponseFontendDefine.CODE_SUCCESS);
            }
            return responseModel;
        } catch (Exception e){
            throw handleException(e);
        } finally {
            sw.stop();
            log.info(END_LOG, action, sw.getTotalTimeSeconds());
        }
    }

    @PostMapping()
    public ResponseModel doCreate(@RequestBody List<ObjectDetail> entity, HttpServletRequest request){
        final String action = "doCreate";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            entity.forEach(item ->{
                if (item.getCol() == null){
                    String maxCol = service.getMaxColByObj(item.getObj());
                    String no = maxCol.split("_")[(maxCol.split("_")).length - 1];
                    String col = "col_" + String.valueOf(Integer.parseInt(no) + 1);
                    item.setCol(col);
                }
                item.setStatus(1);
                item.setCreatedBy(getUserFromToken(request));
                item.setUpdatedBy(getUserFromToken(request));
                service.create(item);
            });
            ResponseModel responseModel = new ResponseModel();
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
    public ResponseModel doDelete(@PathVariable("id") Integer id, HttpServletRequest request){
        final String action = "doDelete";
        StopWatch sw = new StopWatch();
        sw.start();
        log.info(START_LOG, action);
        try {
            service.delete(id);
            ResponseModel responseModel = new ResponseModel();
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
}
