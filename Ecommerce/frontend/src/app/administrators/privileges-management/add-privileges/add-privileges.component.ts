import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import Swal from 'sweetalert2';
import { PrivilegesManagementService } from '../privileges-management.service';

@Component({
  selector: 'app-add-privileges',
  templateUrl: './add-privileges.component.html',
  styleUrls: ['./add-privileges.component.scss']
})
export class AddPrivilegesComponent implements OnInit {

  @Output() afterCreateRoles = new EventEmitter<string>();
  @Input() childMenus: any[];
  public roleLoading = false;
  public selectedFunc: any;
  public selectedMethod: any;

  public addRolesForm: FormGroup;
  public addRolesFormSubmitted = false;
  public filteredUrl: any[];
  public dataMethod = [{id:"1", name: "DETAIL"}, {id:"2", name: "ADD"}, {id:"3", name: "UPDATE"}, {id:"4", name: "DELETE"}, {id:"5", name: "SEARCH"}, {id:"6", name: "LOCK"}];

  constructor(
    private formBuilder: FormBuilder,
    private service: PrivilegesManagementService,
    public _translateService: TranslateService
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.selectedMethod = 'GET';
  }

  addRoles(){
    this.addRolesFormSubmitted = true;
    
    if (this.addRolesForm.invalid) {
      return;
    }

    let content= this.addRolesForm.value;
    // content.roleId = this.addRolesForm.value.role.id;

    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .createRoles(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.ADD_SUCCESS'),
          }).then((result) => {
            // this.initForm();
            this.afterCreateRoles.emit('completed');
          });
        } else if(response.code ===3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.CODE_EXISTED'),
          }).then((result) => {
          });
        }else if(response.code ===100){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.NAME_EXISTED'),
          }).then((result) => {
          });
        }
        else {
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

  initForm(){
    this.addRolesForm = this.formBuilder.group(
      {
        code: ['',[Validators.required]],
        name: ['',[Validators.required]],
        menuID: ['',[Validators.required]],
        method: ['',[Validators.required]],
        url: ['',[Validators.required]],
      },
    );
  }

  get AddRolesForm(){
    return this.addRolesForm.controls;
  }

  resetForm(){
    this.ngOnInit();
  }

  onChange(deviceValue) {
    console.log(deviceValue);
    
    if(deviceValue !== undefined){
      let idMenu = deviceValue.id;
      this.addRolesForm.patchValue({url: ''});
      this.filteredUrl = this.childMenus.find(user => user.id == idMenu);
      if(this.filteredUrl !== undefined){
        this.addRolesForm.patchValue({url: this.filteredUrl['url']})
      }
    }
  }

}
