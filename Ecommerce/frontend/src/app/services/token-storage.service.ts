import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin, Observable, of } from 'rxjs';
import { CredentialModel } from '../auth/models/credential.model';

export enum StorageKey {
  ACCESS_TOKEN = 'accessToken',
  REFRESH_TOKEN = 'refreshToken',
  USER_ROLES = 'userRoles',
  USER_PROFILE = 'userProfile',
  USER_TYPE = 'userType',
  USER_NAME = 'name',
  MENU_LIST = 'menuList',
  PROD_PLAN = 'selectedProductionPlan',
  // DatND
  USER_LOGINID = 'loginId',
  USER_PRIVILEGES = 'privileges',
  ACTION = 'action',
  LOGIN_TYPE = 'loginType',
}

export enum USER_TYPE {
  ADMIN = 'ADMIN',
  CUSTOMER = 'CUSTOMER'
}

@Injectable()
export class TokenStorage {
  notifyChange: BehaviorSubject<any> = new BehaviorSubject<any>({});

  public getAccessToken(): Observable<string> {
    const token: string = localStorage.getItem(StorageKey.ACCESS_TOKEN) as string;
    return of(token);
  }

  public getTokenStr(): string {
    return localStorage.getItem(StorageKey.ACCESS_TOKEN);
  }

  public getRefreshToken(): Observable<string> {
    const token: string = localStorage.getItem('refreshToken') as string;
    return of(token);
  }

  public getUserType(): Observable<string> {
    const userType: any = localStorage.getItem(StorageKey.USER_TYPE);
    return of(userType);
  }

  public setAccessToken(token: string): TokenStorage {
    localStorage.setItem(StorageKey.ACCESS_TOKEN, token);
    return this;
  }

  public setUsername(name: string): TokenStorage {
    localStorage.setItem(StorageKey.USER_NAME, name);
    return this;
  }

  // DatND
  public getUsername(): string {
    const userName: any = localStorage.getItem(StorageKey.USER_NAME);
    return userName;
  }
  public setUserLoginId(loginId: string): TokenStorage {
    localStorage.setItem(StorageKey.USER_LOGINID, loginId);
    return this;
  }
  public getUserLoginId(): string {
    const loginId: any = localStorage.getItem(StorageKey.USER_LOGINID);
    return loginId;
  }


  public setUserListPrivilege(listPrivilege: any): TokenStorage {
    let listObjStr = ''
    for (let i = 0; i < listPrivilege.length; i++) {
      const objStr = JSON.stringify(listPrivilege[i])
      listObjStr += objStr + '|'
      if (i == listPrivilege.length - 1)
        listObjStr += objStr
    }
    localStorage.removeItem(StorageKey.USER_PRIVILEGES)
    localStorage.setItem(StorageKey.USER_PRIVILEGES, listObjStr);
    return this;
  }
  public getUserListPrivilege(): string {
    const listPrivilege: any = localStorage.getItem(StorageKey.USER_PRIVILEGES);
    return listPrivilege;
  }
  // End DatND

  public setRefreshToken(token: string): TokenStorage {
    localStorage.setItem('refreshToken', token);
    return this;
  }

  public setRoleAdmin(adm: string): TokenStorage {
    localStorage.setItem('ADM', adm);
    return this;
  }

  public setUserType(userType: string): any {
    if (userType != null) {
      localStorage.setItem(StorageKey.USER_TYPE, userType);
    }
    return this;
  }

  public setUserProfile(profile: any): any {
    if (profile != null) {
      localStorage.setItem(StorageKey.USER_PROFILE, JSON.stringify(profile));
    }
    return this;
  }

  public getUserProfile(): CredentialModel {
    const userInfo: any = localStorage.getItem(StorageKey.USER_PROFILE);
    try {
      const item = JSON.parse(userInfo);
      return {
        userId: item.userId,
        email: item.email,
        fullName: `${item.firstName} ${item.lastName}`,
        phone: item.phone,
      };
    } catch (e) {
      console.log(e)
      return undefined;
    }
  }

  public setUserMenu(menu: any): any {
    if (menu != null) {
      localStorage.setItem(StorageKey.MENU_LIST, JSON.stringify(menu));
    }
    return this;
  }

  public getUserMenu(): any[] {
    const menu: any = localStorage.getItem(StorageKey.MENU_LIST);
    try {
      return JSON.parse(menu);
    } catch (e) {
      return [];
    }
  }

  public setUserRoles(roles: string[]): any {
    if (roles != undefined) {
      localStorage.setItem(StorageKey.USER_ROLES, roles.join("|"));
    }
    return this;
  }

  public getUserRole(): string {
    return localStorage.getItem(StorageKey.USER_ROLES);
  }

  public setLoginType(loginType: string): TokenStorage {
    localStorage.setItem(StorageKey.LOGIN_TYPE, loginType);
    return this;
  }
  public getLoginType(): string {
    const loginType: any = localStorage.getItem(StorageKey.LOGIN_TYPE);
    return loginType;
  }

  public removeItem(key: string): Observable<void> {
    return of(localStorage.removeItem(key));
  }

  public setAction(action: any): TokenStorage {
    if (action != null) {
      localStorage.setItem(StorageKey.ACTION, JSON.stringify(action));
    }
    return this;
  }

  public getAction(): any {
    const action: any = localStorage.getItem(StorageKey.ACTION);
    return action;
  }

  public clear(): Observable<any> {
    const deleteKeys = [StorageKey.ACCESS_TOKEN,
    StorageKey.USER_PROFILE, StorageKey.USER_ROLES,
    StorageKey.MENU_LIST, StorageKey.PROD_PLAN];
    const taskRemove$: any[] = [];
    deleteKeys.forEach(key => {
      taskRemove$.push(this.removeItem(key));
    });
    return forkJoin(taskRemove$);
  }
}
