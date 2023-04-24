import { Injectable } from "@angular/core";
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from "@angular/router";

import { AuthenticationService } from "app/auth/service";

@Injectable({ providedIn: "root" })
export class AuthGuard implements CanActivate {
  public whiteList = [
    "/pages/account-settings",
    "/dashboard",
    "/exhibition/proof/search",
    "/privileges",
    "/",
  ];

  /**
   *
   * @param {Router} _router
   * @param {AuthenticationService} _authenticationService
   */
  constructor(
    private _router: Router,
    private _authenticationService: AuthenticationService
  ) {}

  // canActivate
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const currentUser = this._authenticationService.currentUserValue;

    if (currentUser) {
      // check if route is restricted by role
      if (
        route.data.roles &&
        route.data.roles.indexOf(currentUser.role) === -1
      ) {
        // role not authorised so redirect to not-authorized page
        this._router.navigate(["/pages/miscellaneous/not-authorized"]);
        return false;
      }
      // Check phan quyen menu
      // console.log(localStorage.getItem("menu"))
      // console.log(state.url)
      if (!state.url.startsWith("/exhibition/proof/search")) {
        if (!this.whiteList.includes(state.url)) {
          if (
            !localStorage.getItem("menu").includes('"url":"' + state.url + '"')
          ) {
            this._router.navigate(["/pages/miscellaneous/not-authorized"]);
            return false;
          }
        }
      }

      // authorised so return true
      return true;
    }
    // if(state.url.){

    // }

    // not logged in so redirect to login page with the return url
    this._router.navigate(["/pages/authentication/login-v2"], {
      queryParams: { returnUrl: state.url },
    });
    return false;
  }
}
