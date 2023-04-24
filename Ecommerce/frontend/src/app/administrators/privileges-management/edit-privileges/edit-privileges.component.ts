import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import Swal from 'sweetalert2';
import { PrivilegesManagementService } from '../privileges-management.service';

@Component({
  selector: 'app-edit-privileges',
  templateUrl: './edit-privileges.component.html',
  styleUrls: ['./edit-privileges.component.scss']
})
export class EditPrivilegesComponent implements OnInit {

  @Output() afterEditRoles = new EventEmitter<string>();
  @Input() editMenus: any[];

  public addRolesForm: FormGroup;
  public addRolesFormSubmitted = false;
  public roleId;
  public data;
  public dataMenu: any[];
  public dataMethod = [{id:"1", name: "DETAIL"}, {id:"2", name: "ADD"}, {id:"3", name: "UPDATE"}, {id:"4", name: "DELETE"}, {id:"5", name: "SEARCH"}, {id:"6", name: "LOCK"}];

  constructor(
    private formBuilder: FormBuilder,
    private service: PrivilegesManagementService,
    public _translateService: TranslateService
  ) { }

  ngOnInit(): void {
    this.initForm()
    this.roleId = window.localStorage.getItem("roleId");
    this.getRolesDetail();
    console.log(this.data);
    
    this.addRolesForm.get('code').disable();
  }

  initForm() {
    this.addRolesForm = this.formBuilder.group(
      {
        id: [''],
        code: ['', [Validators.required]],
        name: ['', [Validators.required]],
        menuID: ['', [Validators.required]],
        method: ['', [Validators.required]],
        url: ['', [Validators.required]],
      },
    );
  }

  fillForm() {
    this.addRolesForm.patchValue(
      {
        id: this.data.id,
        code: this.data.code,
        name: this.data.name,
        menuID: this.data.menuID,
        method: this.data.method,
        url: this.data.url,
      },
    );
  }

  onChange(deviceValue) {
    if(deviceValue !== undefined){
      this.addRolesForm.patchValue({url: ''})
    this.dataMenu = this.editMenus.find(user => user.id == deviceValue.id);
    if(this.dataMenu !== undefined){
      this.addRolesForm.patchValue({url: this.dataMenu['url']})
    }
    }
  }

  get AddRolesForm() {
    return this.addRolesForm.controls;
  }

  async getRolesDetail() {
    if (this.roleId !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailRoles(params, this.roleId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.fillForm();
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

  editRoles() {
    this.addRolesFormSubmitted = true;
    if (this.addRolesForm.invalid) {
      return;
    }

    let content = this.addRolesForm.value;
    console.log(content);

    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editlRoles(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.UPDATE_SUCCESS'),
          }).then((result) => {
            this.afterEditRoles.emit('completed');
          });
        }else if(response.code ===100){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.NAME_EXISTED'),
          }).then((result) => {
          });
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

  resetForm() {
    this.ngOnInit();
  }

}
