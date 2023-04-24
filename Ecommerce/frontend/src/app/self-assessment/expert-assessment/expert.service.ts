import { TokenStorage } from 'app/services/token-storage.service';
import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
@Injectable({
  providedIn: 'root'
})
export class ExpertService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/assessment";
  private readonly API_URL_FILE = this.httpUtilService.BASE_URL_EDITOR + this.httpUtilService.api.base + "/assessment/wopi/files/";
  private readonly PRO_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/programs";
  private readonly USER_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/user-info";
  constructor(private httpUtilService:HttpUtilService, private tokenStorage: TokenStorage) { }
  
  public async searchAssessment(params: any){
    return await this.httpUtilService.callAPI(this.API_URL+"/expert", params).toPromise();
  }

  public getURLEditor(id: number){
    return this.httpUtilService.EDITOR_URL + "WOPISrc=" + this.API_URL_FILE + id + "?access_token=" + this.tokenStorage.getTokenStr();
  }

  public async doSave(params: any){
    return await this.httpUtilService.callAPI(this.API_URL+"/update-experts", params).toPromise();
  }

  // Dùng cho xem chi tiết theo ngôn ngữ
  public async detailAssessment(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/detail/" + id, params).toPromise();
  }

  // Dùng cho cập nhật
  public async getAssessmentById(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async download(id: number, fileName: string){
    return await this.httpUtilService.onDownloadFile(this.API_URL + "/download/" + id, fileName);
  }

  public async deleteAssessment(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }
  public async getListPrograms(params: any){
    return await this.httpUtilService.callAPI(this.PRO_URL + "/selectbox", params).toPromise();
  }
  public async getListUserByRole(params: any){
    return await this.httpUtilService.callAPI(this.USER_URL + "/get-list-by-role" , params).toPromise();
  }
}
