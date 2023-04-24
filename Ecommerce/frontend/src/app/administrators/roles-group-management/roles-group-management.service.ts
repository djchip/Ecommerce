import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class RolesGroupManagementService {

  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles";

  constructor(private httpUtilService: HttpUtilService) { 

  }
  public async addRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async editRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async searchRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async detailRoles(params: any, id:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async deleteRoles(params: any, roleId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete/" + roleId, params).toPromise();
  }

  public async lockRoles(params: any, roleId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/lock/" + roleId, params).toPromise();
  }

  public async unLockRoles(params: any, roleId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/unlock/" + roleId, params).toPromise();
  }

  public async getListRoles(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-role", params).toPromise();
  }
  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }
}
