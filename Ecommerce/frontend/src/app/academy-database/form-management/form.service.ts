import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import { Observable } from 'rxjs';
import { HttpClient, HttpEvent, HttpRequest  } from '@angular/common/http';
import { TokenStorage } from 'app/services/token-storage.service';



@Injectable({
  providedIn: 'root'
})
export class FormService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/form";
  private readonly API_URL_FILE_DATABASE = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/file-database";
  private readonly API_UNIT = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/unit/get-unit";
  private readonly API_DB_OBJECT = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/database-object";
  private url = this.httpUtilService.SOCKET + "form";
  private readonly API_URL_FILE = this.httpUtilService.BASE_URL_EDITOR + this.httpUtilService.api.base + "/form/wopi/files/";

  constructor(
    private httpUtilService: HttpUtilService, 
    private http: HttpClient, 
    private tokenStorage: TokenStorage) { }

  public getURLEditor(id: number){
    return this.httpUtilService.EDITOR_URL + "WOPISrc=" + this.API_URL_FILE + id + "?access_token=" + this.tokenStorage.getTokenStr();
  }

  public async getTotalFormByYear(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getTotalFormByYear", params).toPromise();
  }

  public async download(id: number, fileName: string) {
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/download/" + id, fileName);
  }

  public async downloadDB(id: number, fileName: string) {
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/downloadDB/" + id, fileName);
  }
  public async getUnit(params: any){
    return await this.httpUtilService.callAPI(this.API_UNIT, params).toPromise();
  }
  public async searchDbObject(params: any){
    return await this.httpUtilService.callAPI(this.API_DB_OBJECT, params).toPromise();
  }
  public async getListForm(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-menu", params).toPromise();
  }
  public async getListFormUploaded(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getListFormUploaded", params).toPromise();
  }

  public async getTotalForm(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getTotalForm", params).toPromise();
  }

  public async getListFormNotUploaded(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getListFormNotUploaded", params).toPromise();
  }
  public async searchForm(params: any){
    return await this.httpUtilService.callAPI(this.API_URL , params).toPromise();
  }
  public async searchDatabase(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/database", params).toPromise();
  }
  public async deletForm(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }
  public async addForm(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add" , params).toPromise();
  }
  public async copyForm(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/copy" , params).toPromise();
  }
  public async uploadForm(params: any, file: File) {
    return await this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/data-source/import-excel", file, params).toPromise();
  }
  public async copyForms(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/copy-multi" , params).toPromise();
  }
  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi" , params).toPromise();
  }
  public async deleteMultiCSDL(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi-csdl" , params).toPromise();
  }
  public async detailForm(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async detailFormDB(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-form/" + id, params).toPromise();
  }

  public async editForm(params: any){
    return await this.httpUtilService.callAPI(this.API_URL +"/edit", params).toPromise();
  }

  public async editFormWithoutFile(params: any){
    return await this.httpUtilService.callAPI(this.API_URL +"/edit-without-file", params).toPromise();
  }

  public async saveFileDatabase(params: any){
    return await this.httpUtilService.callAPI(this.API_URL_FILE_DATABASE +"/edit", params).toPromise();
  }

  public async exportForm(fileName:string ){
    return await this.httpUtilService.onDownloadFile(this.API_URL +"/export",fileName);
  }


  // public async importForm(params:any){
  //   return await this.httpUtilService.onDownloadFile(this.API_URL +"/import",params);
  // }

  pushFileToStorage(file: File): Observable<HttpEvent<{}>> {
    const data: FormData = new FormData();

    data.append('file', file);

    const newRequest = new HttpRequest('POST', `${this.url}/import`, data, {
      reportProgress: true,
      responseType: 'text',
    });

    return this.http.request(newRequest);
  }

}