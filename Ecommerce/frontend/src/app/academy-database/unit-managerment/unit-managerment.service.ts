import { HttpClient, HttpEvent, HttpRequest  } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UnitManagementService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/unit";
  private baseUrl = '/unit';
  private url = this.httpUtilService.SOCKET + "unit";
  list:  any=[];

  constructor(
    private httpUtilService: HttpUtilService,
    private http: HttpClient
  ) { }

  public async addUnit(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add-unit", params).toPromise();
  }

  public async editUnit(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/update-unit", params).toPromise();
  }

  public async searchUnit(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-all", params).toPromise();
  }

  public async detailUnit(params: any, unitId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/detail-unit/" + unitId, params).toPromise();
  }

  public async deleteUnit(params: any, unitId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-unit/" + unitId, params).toPromise();
  }

  public async export(filename: string){
    return await this.httpUtilService.exportUnitToExcel(this.API_URL + "/export" , filename);
  }

  public async exportEn(filename: string){
    return await this.httpUtilService.exportUnitToExcel(this.API_URL + "/exportEn" , filename);
  }
  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
  
  // getExcelData(){
  //   return this.http.get<any>(`${this.url}/export`, {responseType: 'arraybuffer' as 'json'})
  // }
  
  pushFileToStorage(file: File): Observable<HttpEvent<{}>> {
    const data: FormData = new FormData();

    data.append('file', file);

    const newRequest = new HttpRequest('POST', `${this.url}/import-excel`, data, {
      reportProgress: true,
      responseType: 'text',
    });

    return this.http.request(newRequest);
  }
  public async importUnit(file:File, params: any) {
    return this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/unit/import-excelll", file ,params).toPromise();
  }

  
  public async formatObj(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-objs", params).toPromise();
  }
}
