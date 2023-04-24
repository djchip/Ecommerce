import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/dashboard";
  private readonly PRO_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/programs";
  private readonly PROOF_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/proof";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async getListStoryMaxMonth(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getListStoryMaxMonth", params).toPromise();
  }

  public async getQuestionsNoanswer(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/getQuestionsUnsolved", params).toPromise();
  }

  public async notifyTrainer(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/save-handler", params).toPromise();
  }

  public async getListPrograms(params: any){
    return await this.httpUtilService.callAPI(this.PRO_URL + "/get-all-programs", params).toPromise();
  }

  public async getListQuantityProofHasFile(params: any){
    return await this.httpUtilService.callAPI(this.PROOF_URL + "/getListQuantityProofHasFile", params).toPromise();
  }

  public async getListQuantityProofHasNotFile(params: any){
    return await this.httpUtilService.callAPI(this.PROOF_URL + "/getListQuantityProofHasNotFile", params).toPromise();
  }

  public async getListYearInDataBase(params: any){
    return await this.httpUtilService.callAPI(this.PRO_URL + "/getListYearInDataBase", params).toPromise();
  }

  public async getProgramQuantityByYear(params: any){
    return await this.httpUtilService.callAPI(this.PRO_URL + "/getProgramQuantityByYear", params).toPromise();
  }

  public async getYearNow(params: any){
    return await this.httpUtilService.callAPI(this.PRO_URL + "/getYearNow", params).toPromise();
  }

}
