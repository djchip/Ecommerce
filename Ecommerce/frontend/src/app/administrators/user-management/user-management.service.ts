import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/user-info";

  private readonly ROLE_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles";

  private readonly UNIT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/unit";

  constructor(
    private httpUtilService: HttpUtilService
  ) { }

  public async addUser(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async editUser(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async searchUser(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async detailUser(params: any, userId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + userId, params).toPromise();
  }

  public async deleteUser(params: any, userId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + userId, params).toPromise();
  }

  public async lockUser(params: any, userId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/lock/" + userId, params).toPromise();
  }

  public async unLockUser(params: any, userId:number){
    return await this.httpUtilService.callAPI(this.API_URL + "/unlock/" + userId, params).toPromise();
  }

  public async getListRole(params: any){
    return await this.httpUtilService.callAPI(this.ROLE_URL + "/get-role", params).toPromise();
  }

  public async getListUserByRole(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/get-list-by-role" , params).toPromise();
  }

  public async resetPassword(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/reset-password" , params).toPromise();
  }

  public async getListUnit(params: any){
    return await this.httpUtilService.callAPI(this.UNIT_URL + "/listActiveUnits", params).toPromise();
  }

  public async deleteMulti(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/delete-multi", params).toPromise();
  }

  public async getUserIdByRolesid(params: any, username:string){
    return await this.httpUtilService.callAPI(this.API_URL + "/getUserIdByRolesid?username=" + username, params).toPromise();
  }
}
