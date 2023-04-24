import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class PrivilegesProStaCriService {
  private readonly STANDARD_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/standard-criteria";
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/criteria-user";

  constructor(
    private httpUtilService: HttpUtilService, 
  ) { }

  public async getTree(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD_URL + "/setupTreeProgramStandardCriteriaByOrgId/" + id, params).toPromise();
  }

  public async getTreeEn(params: any, id:any){
    return await this.httpUtilService.callAPI(this.STANDARD_URL + "/setupTreeProgramStandardCriteriaByOrgIdEn/" + id, params).toPromise();
  }

  public async getListCriDTOByUserId(params: any, userId: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + userId, params).toPromise();
  }
}
