import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class RolePrivilegesService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles-privileges";
  private readonly API_URL_ROLE = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles";
  private readonly API_URL_PRIVILEGES= this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/privileges";

  constructor(private httpUtilService: HttpUtilService) { }

  public async doSearch(params: any) {
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
}

public async searchPrivileges(params: any) {
    return await this.httpUtilService.callAPI(this.API_URL_PRIVILEGES, params).toPromise();
}

public async doSearchDetail(rolePrivilegesId: number, params: any) {
    return await this.httpUtilService.callAPI(this.API_URL + "/get?id=" + rolePrivilegesId, params).toPromise();
}

public async doUpdate(params: any) {
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
}

public async doDelete(params: any, rolePrivilegesId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete/" + rolePrivilegesId, params).toPromise();
  }


public async searchRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL_ROLE, params).toPromise();
  }
}
