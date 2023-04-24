import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { HttpUtilService } from 'app/services/http-util.service';

import { BehaviorSubject, Observable } from 'rxjs';

@Injectable()
export class AccountSettingsService implements Resolve<any> {
  rows: any;
  onSettingsChanged: BehaviorSubject<any>;
  private readonly ROLE_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/roles";
  private readonly UNIT_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/unit";


private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/user-info";
  /**
   * Constructor
   *
   * @param {HttpClient} _httpClient
   */
  constructor(private _httpClient: HttpClient,  private httpUtilService: HttpUtilService) {
    // Set the defaults
    this.onSettingsChanged = new BehaviorSubject({});
  }

  public async editUser(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }
 
  public async findbyName(params: any, username:string){
    return await this.httpUtilService.callAPI(this.API_URL + "/find-by-username?username=" + username, params).toPromise();
  }

  public async changepassword(params: any){
    return await this.httpUtilService.callAPI(this.API_URL + "/change-password" , params).toPromise();
  }

  

  public async getListRole(params: any){
    return await this.httpUtilService.callAPI(this.ROLE_URL + "/get-role", params).toPromise();
  }
  /**
   * Resolver
   *
   * @param {ActivatedRouteSnapshot} route
   * @param {RouterStateSnapshot} state
   * @returns {Observable<any> | Promise<any> | any}
   */
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> | Promise<any> | any {
    return new Promise<void>((resolve, reject) => {
      Promise.all([this.getDataTableRows()]).then(() => {
        resolve();
      }, reject);
    });
  }

  /**
   * Get rows
   */
  getDataTableRows(): Promise<any[]> {
    return new Promise((resolve, reject) => {
      this._httpClient.get('api/account-settings-data').subscribe((response: any) => {
        this.rows = response;
        this.onSettingsChanged.next(this.rows);
        resolve(this.rows);
      }, reject);
    });
  }

  public async getListUnit(params: any){
    return await this.httpUtilService.callAPI(this.UNIT_URL + "/listActiveUnits", params).toPromise();
  }
}
