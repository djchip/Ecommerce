import { HttpClient } from '@angular/common/http';
import { HttpUtilService } from './../../services/http-util.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProofManagementService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/proof";

  private readonly COLLECT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/file";

  private readonly UNIT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/unit";
  
  private readonly STANDARD_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/directory";
  
  private readonly STANDARD1_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/standard-criteria";

  private readonly DOCUMENT_FIELD_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/document-field";

  private readonly READ_FILE = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/read-file";

  constructor(
    private httpUtilService: HttpUtilService, 
    private http: HttpClient
  ) { }

  public async searchProof(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async searchProofWithAdmin(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/with-admin", params).toPromise();
  }

  public async getListCriteriaByStandard(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-list-cri-by-staId", params).toPromise();
  }

  public async getFileNameByProofId(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-file-name-by-proofId", params).toPromise();
  }

  public async getUnitByCreater(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-unit-by-creater", params).toPromise();
  }

  public async addProof(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add", params).toPromise();
  }

  public async privilegesProStaCri(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/privilegesProStaCri", params).toPromise();
  }

  public async copyDocumentToProof(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/copy-document-to-proof", params).toPromise();
  }

  public async copyFormToProof(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/copy-form-to-proof", params).toPromise();
  }

  public async copyProof(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/copy", params).toPromise();
  }

  public async editProof(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/update", params).toPromise();
  }

  public async saveProof(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL, file, params).toPromise();
  }

  public async addSingleFile(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/upload-single-file", file, params).toPromise();
  }

  public async uploadFileSearchWeb(params: any){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/upload-file-search-web", null,params).toPromise();
  }

  public async addSingleFileWithoutFileContent(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/upload-single-file-no-content", file, params).toPromise();
  }

  public async updateFile(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/update-file", file, params).toPromise();
  }

  public async updateFileWithoutFileContent(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/update-file-no-content", file, params).toPromise();
  }

  public async addMultipleFile(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/upload-multiple-file", file, params).toPromise();
  }

  public async saveFile(file: File){
    return await this.httpUtilService.callAPI(this.API_URL, file).toPromise();
  }

  public async getListUnit(params: any){
    return await this.httpUtilService.callAPI(this.UNIT_URL + "/get-unit", params).toPromise();
  }

  public async getListDocumentType(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-list-documentType", params).toPromise();
  }

  public async getListField(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-list-field", params).toPromise();
  }

  public async getListUnitSelected(params: any, listUnitId: any){
    return await this.httpUtilService.callAPI(this.UNIT_URL + "/" + listUnitId, params).toPromise();
  }

  public async getListStandard(params: any){
    return await this.httpUtilService.callAPI(this.STANDARD_URL + "/list-all", params).toPromise();
  }

  public async deleteProof(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async deleteFile(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/deleteFile/" + id, params).toPromise();
  }

  public async detailProof(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async download(id: number, fileName: string){
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/download/" + id, fileName);
  }

  public async getTree(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD1_URL + "/setupTreeStandardCriteriaByProgramId/" + id, params).toPromise();
  }

  public async getTreeEn(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD1_URL + "/setupTreeStandardCriteriaByProgramIdEn/" + id, params).toPromise();
  }

  public async getTreeSta(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD1_URL + "/getTreeStandard/" +id, params).toPromise();
  }

  public async getTreeStaEn(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD1_URL + "/getTreeStandardEn/" +id, params).toPromise();
  }

  public async getExhibitionCode(params: any){
    return await this.httpUtilService.callAPI(this.STANDARD1_URL + "/getExhibitionCode", params).toPromise();
  }

  public getUploadFileApiUrl(){
    return this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/file/upload";
  }

  public async collectExhibition(params: any, file:File){
    return await this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/file/upload", file, params).toPromise();
  }

  public async getCollectedExhibition(params: any){
    return await this.httpUtilService.callAPI(this.COLLECT_URL, params).toPromise();
  }

  public async autoUpload(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/auto-upload", params).toPromise();
  }

  public async getCodeSta(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/gen-code-sta/", params).toPromise();
  }

  public async getCodeCri(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/gen-code-cri/", params).toPromise();
  }

  public async getListStaAndListCriSelected(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getListStaAndListCriSelected", params).toPromise();
  }

  public async formatCode(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-obj", params).toPromise();
  }

  public async formatObjs(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-objs", params).toPromise();
  }

  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
}
