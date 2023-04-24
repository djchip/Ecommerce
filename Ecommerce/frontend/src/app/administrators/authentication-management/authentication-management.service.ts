import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationManagementService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/role-privileges";
  private readonly MENU_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/menu";
  private readonly RoleMenu_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/role-menu";
  private readonly USER_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/user-info";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async getTree(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async getPrivilegesByRoleId(params: any, roleId: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + roleId, params).toPromise();
  }

  public async updatePrivileges(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }
  public async getListMenu(params: any){
    return await this.httpUtilService.callAPI(this.MENU_URL +"/tree-menu", params).toPromise();
  }
  public async updateRoleMenu(params: any){
    return await this.httpUtilService.callAPI(this.RoleMenu_URL, params).toPromise();
  }
  public async getMenuByRoleId(params: any, roleId: any){
    return await this.httpUtilService.callAPI(this.RoleMenu_URL + "/" + roleId, params).toPromise();
  }

  public async getUserIdByRolesid(params: any, username:string){
    return await this.httpUtilService.callAPI(this.USER_URL + "/getUserIdByRolesid?username=" + username, params).toPromise();
  }

}
