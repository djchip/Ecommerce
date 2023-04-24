import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpUtilService } from './../../services/http-util.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/document";

  constructor(private httpUtilService: HttpUtilService, private http: HttpClient) { }

  public async getListDocument(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/list-document", params).toPromise();
  }

  public async getIdFileDTO(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getIdFileDTO", params).toPromise();
  }

  public async searchDocument(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async detailDocument(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async getUnitByCreater(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-unit-by-creater", params).toPromise();
  }

  public async deleteDocument(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async getFilenameById(params: any, id:any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-filename-by-id/" + id, params).toPromise();
  }

  public async addDocument(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add", params).toPromise();
  }

  public async addSingleFile(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/upload-file", file, params).toPromise();
  }

  public async editDocument(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/update", params).toPromise();
  }

  public  async  saveDocument(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndData(this.API_URL + "/add", file, params).toPromise();
  }

  public async download(id: number, fileName: string){
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/download/" + id, fileName);
  }

  public async export(fileName: string, type: string){
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/export/" + type, fileName);
  }

  public async exportExcel(fileName: string, type: string, ids: string){
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/export/" + type + "/" + ids, fileName);
  }

  public async updateFile(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/update-file", file, params).toPromise();
  }

  public async updateFileWithoutFileContent(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndDataOfProof(this.API_URL + "/update-file-no-content", file, params).toPromise();
  }
  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
}
