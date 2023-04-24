import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
    providedIn: 'root'
})
export class RoleMenuPrivilegesService {
    private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles-menus";
    private readonly API_URL_ROLE = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles";
    private readonly API_URL_MENU = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/menu";

    constructor(
        private httpUtilService: HttpUtilService
    ) { }

    public async doSearch(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
    }

    public async searchMenu(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL_MENU, params).toPromise();
    }

    public async doSearchDetail(roleMenuId: number, params: any) {
        return await this.httpUtilService.callAPI(this.API_URL + "/get?id=" + roleMenuId, params).toPromise();
    }

    public async doUpdate(params: any) {
        return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
    }

    // public async doDelete(roleMenuId: number, params: any) {
    //     return await this.httpUtilService.callAPI(this.API_URL +"/detele?id=" +roleMenuId, params).toPromise();
    // }

    public async doDelete(params: any, roleMenuId:number){
        return await this.httpUtilService.callAPI(this.API_URL + "/delete/" + roleMenuId, params).toPromise();
      }
    

    public async searchRoles(params: any){
        return await this.httpUtilService.callAPI(this.API_URL_ROLE, params).toPromise();
      }
}