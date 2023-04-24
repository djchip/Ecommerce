import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
  // Public
  public apiData = [];
  public onApiDataChange: BehaviorSubject<any>;
  private readonly API_URL = this.httpUtilService.BASE_URL + this.httpUtilService.api.base + "/notifications";

  /**
   *
   * @param {HttpClient} _httpClient
   */
  constructor(private _httpClient: HttpClient, private httpUtilService: HttpUtilService) {
    this.onApiDataChange = new BehaviorSubject('');
    // this.getNotificationsData();
  }

  /**
   * Get Notifications Data
   */
  getNotificationsData(): Promise<any[]> {
    return new Promise((resolve, reject) => {
      this._httpClient.get('api/notifications-data').subscribe((response: any) => {
        this.apiData = response;
        this.onApiDataChange.next(this.apiData);
        resolve(this.apiData);
      }, reject);
    });
  }

  public async getListNotifications(params: any){
    return await this.httpUtilService.callAPI(this.API_URL, params).toPromise();
  }

  public async seenNotification(params: any, id){
    return await this.httpUtilService.callAPI(this.API_URL + "/" + id, params).toPromise();
  }

  public async seenAll(params: any, username:string){
    return await this.httpUtilService.callAPI(this.API_URL + "/seenAll/" + username, params).toPromise();
  }
}
