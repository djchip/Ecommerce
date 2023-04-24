import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import { Observable } from 'rxjs';
import { HttpClient, HttpEvent, HttpRequest  } from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class CriteriaService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/criteria";
  private readonly STA_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/directory";
  private readonly PRO_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/programs";
  private readonly ORG_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/organization";
  private readonly EXH_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/document-field";
  private readonly CAT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/categories";

  // private readonly CRITERIA_USER_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/criteriaUser";
  private url = this.httpUtilService.SOCKET + "criteria";



  constructor(
    private httpUtilService: HttpUtilService,
    private http: HttpClient


  ) { }

  public async getListOrganization(params: any){
    return await this.httpUtilService.callAPI(this.ORG_URL + "/selectbox", params).toPromise();
  }

  public async getListOrganizationForCriteria(params: any){
    return await this.httpUtilService.callAPI(this.ORG_URL + "/getOrgForCriteria", params).toPromise();
  }

  public async getCodeEXH(params: any, id: any){
    return await this.httpUtilService.callAPI(this.EXH_URL + "/get-code-exh/" + id, params).toPromise();
  }
 

  public async searchStandard(params: any){
    return await this.httpUtilService.callAPI(this.STA_URL + "/select-by-org", params).toPromise();
  }

  public async getListStaByOrgIdAndUsername(params: any){
    return await this.httpUtilService.callAPI(this.STA_URL + "/select-by-orgId", params).toPromise();
  }

  public async getListCriteriaIdByUsername(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getListCriteriaIdByUsername", params).toPromise();
  }

  public async getListStaByProgramId(params: any){
    return await this.httpUtilService.callAPI(this.STA_URL + "/get-listSta-by-programId", params).toPromise();
  }

  public async getListStandard(params: any){
    return await this.httpUtilService.callAPI(this.STA_URL + "/selectbox", params).toPromise();
  }

  public async getListPrograms(params: any){
    return await this.httpUtilService.callAPI(this.PRO_URL + "/selectbox", params).toPromise();
  }

  public async getStandard(params: any, id: number){
    return await this.httpUtilService.callAPI(this.STA_URL + "/" + id, params).toPromise();
  }


  public async getListDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/list-all", params).toPromise();
  }

  public async findAllCriteria(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/find-all", params).toPromise();
  }

  public async getCode(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/auto-code/" + id, params).toPromise();
  }

  public async searchDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL , params).toPromise();
  }
  public async searchDirectoryExcel(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-list-by-id" , params).toPromise();
  }
  public async deletDirectory(params: any, form_id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + form_id, params).toPromise();
  }
  public async addDirectory(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add" , params).toPromise();
  }
  public async detailDirectory(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }
  public async getCriteria(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get/" + id, params).toPromise();
  }

  public async getCodeDirectory(params: any, id: any){
    return await this.httpUtilService.callAPI(this.STA_URL + "/" + id, params).toPromise();
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

  public async formatCode(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-obj", params).toPromise();
  }

  public async formatObj(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-objs", params).toPromise();
  }

  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
  public async getLisCategories(params: any){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/selectbox", params).toPromise();
  }

  public async getLisCategorie(params: any){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/selectCategory-by-Org", params).toPromise();
  }

  
  public async findbyId(params: any){
    return await this.httpUtilService.callAPI(this.STA_URL + "/getId", params).toPromise();
  }
}