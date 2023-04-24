import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';


@Injectable({
  providedIn: 'root'
})
export class ProgramService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/programs";
  private readonly ORG_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/organization";
  private readonly CAT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/categories";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async addPrograms(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async getListOrganization(params: any){
    return await this.httpUtilService.callAPI(this.ORG_URL + "/selectbox", params).toPromise();
  }

  public async findByOrgId(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/find-by-orgId", params).toPromise();
  }

  public async getListPrograms(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-programs", params).toPromise();
  }

  public async searchPrograms(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async deleteProgram(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete"+"/" + id, params).toPromise();
  }

  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }

  public async detailProgram(params: any, id: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/find-by-id"+"/" + id, params).toPromise();
  }

  public async editPrograms(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async formatPrograms(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-obj", params).toPromise();
  }

  
  public async getLisCategories1(params: any){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/selectbox", params).toPromise();
  }

  public async getLisCategories(params: any){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/selectCategory-by-Org", params).toPromise();
  }

  public async getListOrganizationForCriteria(params: any){
    return await this.httpUtilService.callAPI(this.ORG_URL + "/getOrgForCriteria", params).toPromise();
  }

}

