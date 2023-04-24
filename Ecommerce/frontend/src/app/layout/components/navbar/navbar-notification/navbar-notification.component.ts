import { Component, OnInit } from '@angular/core';
import { TokenStorage } from 'app/services/token-storage.service';
import { NotificationsService } from 'app/layout/components/navbar/navbar-notification/notifications.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-navbar-notification',
  templateUrl: './navbar-notification.component.html'
})
export class NavbarNotificationComponent implements OnInit {

  public messages:[];

  /**
   *
   * @param {NotificationsService} _notificationsService
   */
  constructor(private _notificationsService: NotificationsService, private tokenStorage: TokenStorage,) {
    this.getListNotifications();
  }

  // Lifecycle Hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    
  }

  getListNotifications(){
    let params = {
      method: "GET", username: this.tokenStorage.getUsername()
    };
    this._notificationsService
      .getListNotifications(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.messages = response.content;
        } else {
          this.messages = [];
        }
      })
      .catch((error) => {
        this.messages = []
      });
  }

  readAll(){
    let params = {
      method: "PUT",
    };
    this._notificationsService.seenAll(params, this.tokenStorage.getUsername()).then((data) => {
    })
    .catch((error) => {
      
    });
    this.messages = []
  }

  showNotification(id: number, content : string){
    Swal.fire({
      title: content,
      icon: 'info',
      showCancelButton: true,
      confirmButtonText: 'Đã xem',
      cancelButtonText: 'Giữ lại',
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if (result.value) {
        let params = {
          method: 'PUT'
        }
        this._notificationsService.seenNotification(params, id).then((data) => {
          this.getListNotifications();
        })
        .catch((error) => {
          
        });
      }
    });
  }

}
