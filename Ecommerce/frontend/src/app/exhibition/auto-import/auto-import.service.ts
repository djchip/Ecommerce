import { HttpUtilService } from 'app/services/http-util.service';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AutoImportService {

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async collectExhibition(params: any, file: File) {
    return await this.httpUtilService.uploadMultipartFileWithFormData(this.httpUtilService.api.base + "/proof/import-excel", file, params).toPromise();
  }

  public async getListProofHaveNotFile(params: any){
    return await this.httpUtilService.callAPI(this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/proof/have-not-file", params).toPromise();
  }
}
