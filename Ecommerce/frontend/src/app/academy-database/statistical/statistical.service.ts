import { HttpUtilService } from './../../services/http-util.service';
import { Injectable } from '@angular/core';
import { id } from '@swimlane/ngx-datatable';

@Injectable({
  providedIn: 'root'
})
export class StatisticalService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/statistical";
  private readonly API_DATA_SOURCE = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/data-source/import-report";

  constructor(private httpUtilService: HttpUtilService) { }

  public async doImportTemplate(params: any, file: File){
    return await this.httpUtilService.callAPIUploadFilesAndData(this.API_DATA_SOURCE, file, params).toPromise();
  }

  public async statisticalReport(params: any){
    return await this.httpUtilService.callAPIUploadFilesAndData(this.API_URL , null, params).toPromise();
  }

  public async getLabel(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/label", params).toPromise();
  }

  public async getLabelByKeyForm(params: any, id: number){
    return await this.httpUtilService.callAPI(this.API_URL + "/label/" + id, params).toPromise();
  }

  public async doSearch(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async export(fileName: string, id: number){
    return await this.httpUtilService.onDownloadFileLoad(this.API_URL + "/export/" + id, fileName).toPromise();
  }

  public async exportForm(fileName: string, id: number){
    return await this.httpUtilService.onDownloadFileLoad(this.API_URL + "/export/form/" + id, fileName).toPromise();
  }

  public async doRetrieve(id: number, params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async doRetrieveByForm(id: number, params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/form/" + id, params).toPromise();
  }
}
