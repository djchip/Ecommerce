import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private readonly DOCUMENT_FIELD_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/document-field";
  private readonly ORG_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/organization";

  constructor(
    private httpUtilService: HttpUtilService,
  ) { }

  public async searchDocumentType(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-documentType", params).toPromise();
  }

  public async searchExhCode(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-exh-code", params).toPromise();
  }

  public async searchDateTimeFormat(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-date-format", params).toPromise();
  }

  public async detailDocumentType(params: any, id:number){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/" + id, params).toPromise();
  }

  public async addDocumentType(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/add-document-type", params).toPromise();
  }

  public async addField(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/add-field", params).toPromise();
  }

  public async addExhCode(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/add-exh-code", params).toPromise();
  }

  public async addDateFormat(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/add-date-format", params).toPromise();
  }

  public async editDocumentType(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/edit-document-type", params).toPromise();
  }

  public async saveDateTimeFormat(params: any, id:number){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/save-date-time-format/" + id, params).toPromise();
  }

  public async searchField(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-field", params).toPromise();
  }

  public async deleteDocumentType(params: any, id:number){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/" + id, params).toPromise();
  }

  public async getListDocumentType(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-list-documentType", params).toPromise();
  }

  public async getDateFormatSelected(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-date-format-selected", params).toPromise();
  }

  public async getListDateTimeFormat(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-list-date-format", params).toPromise();
  }

  public async getListOrg(params: any){
    return await this.httpUtilService.callAPI(this.ORG_URL + "/get-list-org", params).toPromise();
  }

  public async getListField(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/get-list-field", params).toPromise();
  }

  public async formatCode(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/format-obj", params).toPromise();
  }

  
  public async detailWWW(params: any, id: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/findby"+"/" + id, params).toPromise();
  }

  public async editExhCode(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/edit-exh-code", params).toPromise();
  }
  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.DOCUMENT_FIELD_URL + "/delete-multi", params).toPromise();
  }
}
