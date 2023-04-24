import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class PrivilegesManagementService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/privileges";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async searchRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async createRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/add", params).toPromise();
  }

  public async detailRoles(params: any, roleId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + roleId, params).toPromise();
  }

  public async editlRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/edit" , params).toPromise();
  }

  public async deleteRoles(params: any, roleId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete/" + roleId, params).toPromise();
  }

  public async getLsMenus(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-menus", params).toPromise();
  }
}
