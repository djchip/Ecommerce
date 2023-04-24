import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/organization";
  private readonly CAT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/categories";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async addUser(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async editUser(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async search(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async detailUser(params: any, userId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + userId, params).toPromise();
  }

  public async findOrgByProgramId(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/findOrgByProgramId/" + id, params).toPromise();
  }

  public async findOrgId(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/findOrgId/" + id, params).toPromise();
  }

  public async deleteUser(params: any, userId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + userId, params).toPromise();
  }

  public async getListCategories(params: any){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/selectbox", params).toPromise();
  }

  public async getListCategoriesByOrgId(params: any, orgId: number){
    return await this.httpUtilService.callAPI(this.CAT_URL + "/getListCategoryByOrgId/" + orgId, params).toPromise();
  }

  public async formatCode(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/format-obj", params).toPromise();
  }
  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
}
