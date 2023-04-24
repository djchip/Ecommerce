
import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import { Observable } from 'rxjs';
import { HttpClient, HttpEvent, HttpRequest  } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SoftwareLogService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/software-log";
 
  private url = this.httpUtilService.SOCKET + "software_log";
//   private readonly ROLE_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles";

  constructor(
    private httpUtilService: HttpUtilService,
    private http: HttpClient
  ) { }

  public async AddSoftwareLog(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add", params).toPromise();
  }

  public async editSoftwareLog(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/edit", params).toPromise();
  }

  public async searchSoftwareLog(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async detailSoftwareLog(params: any, Id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + Id, params).toPromise();
  }

  public async deleteSoftwareLog(params: any, Id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + Id, params).toPromise();
  }
  public async addVersion(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add-version", params).toPromise();
  }
  // public async getListVersion(params: any){
  //   return await this.httpUtilService.callAPI(this.API_URL + "/version", params).toPromise();
  // }
  public async undo(params: any,Id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/undo/" + Id, params).toPromise();
  }
  public async importLog (params: any) {
    return await this.httpUtilService.onDownloadFile(this.API_URL + "inmport", params);
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
  import(file: File, params: any): Promise<any> {
    return this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/software-log/import-excel", file ,params).toPromise();
  }
  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
}