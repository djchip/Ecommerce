import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import Swal from 'sweetalert2';
import { PrivilegesManagementService } from '../privileges-management.service';

@Component({
  selector: 'app-detail-privileges',
  templateUrl: './detail-privileges.component.html',
  styleUrls: ['./detail-privileges.component.scss']
})
export class DetailPrivilegesComponent implements OnInit {
  
  @Output() afterDetailRoles = new EventEmitter<string>();

  public roleId;
  public detailData: any;

  public dataMethod = [{id:"1", name: "DETAIL"}, {id:"2", name: "ADD"}, {id:"3", name: "UPDATE"}, {id:"4", name: "DELETE"}, {id:"5", name: "SEARCH"}, {id:"6", name: "LOCK"}];

  constructor(
    private service: PrivilegesManagementService,
    public _translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.roleId = window.localStorage.getItem("roleId");
   this.getRolesDetail();
  }

 getRolesDetail() {
    if (this.roleId !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.service
        .detailRoles(params, this.roleId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.detailData = response.content;
            for(let i = 0; i<this.dataMethod.length; i++){
              if(this.detailData.method == this.dataMethod[i].id){
                this.detailData.method = this.dataMethod[i].name;
              }
            }
          } else {
            Swal.fire({
              icon: "error",
              title: response.errorMessages,
            });
          }
        })
        .catch((error) => {
          Swal.close();
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        });
    }
  }

}
