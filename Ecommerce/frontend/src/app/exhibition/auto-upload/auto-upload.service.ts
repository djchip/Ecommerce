import { HttpUtilService } from './../../services/http-util.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AutoUploadService {

  private readonly COLLECT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/file";
  private readonly EXHIBITION_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/proof/auto-upload";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async getCollectedExhibition(params: any){
    return await this.httpUtilService.callAPI(this.COLLECT_URL, params).toPromise();
  }

  public async collectExhibition(params: any, file: File) {
    return await this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/file/upload", file, params).toPromise();
  }

  public async deleteCollection(params: any){
    return await this.httpUtilService.callAPI(this.COLLECT_URL, params).toPromise();
  }

  public async autoUploadExhibition(params: any){
    return await this.httpUtilService.callAPI(this.EXHIBITION_URL, params).toPromise();
  }

  public async getLink(params: any){
    return await this.httpUtilService.callAPI(this.COLLECT_URL + "/get-link", params).toPromise();
  }

}
