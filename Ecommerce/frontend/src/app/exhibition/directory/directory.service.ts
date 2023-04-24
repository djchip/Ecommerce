import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import { Observable } from 'rxjs';
import { HttpClient, HttpEvent, HttpRequest  } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class DirectoryService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/directory";
  private readonly PRO_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/programs";
  private readonly ORG_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/organization";
  private readonly EXH_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/document-field";
  private readonly USER_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/user-info";
  private readonly CAT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/categories";

  private url = this.httpUtilService.SOCKET + "directory";

  constructor(
    private httpUtilService: HttpUtilService,
    private http: HttpClient
  ) { }

  public async getCodeEXH(params: any, id: any){
    return await this.httpUtilService.callAPI(this.EXH_URL + "/get-code-exh/" + id, params).toPromise();
  }

  public async getListOrganization(params: any){
    return await this.httpUtilService.callAPI(this.ORG_URL + "/selectbox", params).toPromise();
  }

  public async getListPrograms(params: any){
    return await this.httpUtilService.callAPI(this.PRO_URL + "/selectbox", params).toPromise();
  }

  public async getListDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/list-all", params).toPromise();
  }

  public async findAllDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/find-all", params).toPromise();
  }

  public async getAllDirectoryDTO(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/list-all-dto", params).toPromise();
  }

  public async searchDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL , params).toPromise();
  }

  public async searchDirectoryExcel(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/search-by-list-id", params).toPromise();
  }
  public async deletDirectory(params: any, form_id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + form_id, params).toPromise();
  }
  public async addDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add" , params).toPromise();
  }

  public async getListStandardIdByUsername(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getListStandardIdByUsername" , params).toPromise();
  }

  public async detailDirectory(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }
  public async getDirectory(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get/" + id, params).toPromise();
  }

  public async getCode(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/auto-code", params).toPromise();
  }
 
 public async editDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL +"/edit", params).toPromise();
  }

  public async exportDirectoryExcel(fileName:string ){
    return await this.httpUtilService.onDownloadFile(this.API_URL +"/exportExcel",fileName);
  }

  public async exportDirectoryPDF(fileName:string ){
    return await this.httpUtilService.onDownloadFile(this.API_URL +"/exportPDF",fileName);
  }

  public async importDirectory(params:any){
    return await this.httpUtilService.onDownloadFile(this.API_URL +"/import",params);
  }

  pushFileToStorage(file: File): Observable<HttpEvent<{}>> {


    const data: FormData = new FormData();

    data.append('file', file);

    const newRequest = new HttpRequest('POST', `${this.url}/import`, data, {
      reportProgress: true,
      responseType: 'text',
    });

    return this.http.request(newRequest);
  }

  import(file:File, params: any):Promise<any>{
    return this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/directory/import-excel", file ,params).toPromise();
  }

  public async importCriteria(file:File, params: any) {
    return this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/criteria/import-excel", file ,params).toPromise();
  }


  public async formatCode(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-obj", params).toPromise();
  }

  public async formatObj(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-objs", params).toPromise();
  }

  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }

  public async getListUser(params: any){
    return await this.httpUtilService.callAPI(this.USER_URL + "/get-all", params).toPromise();
  }

  public async getListUserWithoutAdmin(params: any){
    return await this.httpUtilService.callAPI(this.USER_URL + "/get-all-without-admin", params).toPromise();
  }

  public async getLisCategories(params: any){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/selectbox", params).toPromise();
  }

  public async getLisCategorie(params: any){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/selectCategory-by-Org", params).toPromise();
  }
}