import { HttpClient } from '@angular/common/http';
import { HttpUtilService } from './../../services/http-util.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LogService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/log";

  constructor(private httpUtilService: HttpUtilService, private http: HttpClient) { }

  public async searchLog(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async detailLog(params: any, id: number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async export(fileName: string, type: string){
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/export/" + type, fileName);
  }
}
