import { Component, OnInit, ViewChild, ViewEncapsulation, ChangeDetectorRef,EventEmitter, Output} from '@angular/core';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { RoleMenuPrivilegesService } from "../role-menu-privileges.service"
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { menu } from 'app/menu/menu';
@Component({
  selector: 'app-edit-role-menu-privileges',
  templateUrl: './edit-role-menu-privileges.component.html',
  styleUrls: ['./edit-role-menu-privileges.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EditRoleMenuPrivilegesComponent implements OnInit {

  @Output() afterEditRoleMenu = new EventEmitter<string>();

  constructor(private formBuilder: FormBuilder, private service: RoleMenuPrivilegesService, private modalService: NgbModal, private router: Router, private dectedChange: ChangeDetectorRef) { }
  public editRoleMenuForm: FormGroup;
  public roleMenuId: string;
  public data = []
  public keyword = "";
  public temp = [];
  public rows = [];
  public roles = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 1000;
  public totalRows = 0;
  selectedRole: any;
  selectedMenu: any;
  ngOnInit(): void {
    this.initForm();
    this.searchRoleMenu();
    this.searchRoles();
    this.roleMenuId = window.localStorage.getItem("roleMenuId");
    this.getRoleMenuDetail() 
  }

  initForm() {
    this.editRoleMenuForm = this.formBuilder.group(
      {
        roleMenuId: ['', Validators.required],
        menu: ['', Validators.required],
        roles: ['', Validators.required],
      },
    );
  }

  get AddUserForm(){
    return this.editRoleMenuForm.controls;
  }


  async getRoleMenuDetail(){
    if(this.roleMenuId !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .doSearchDetail( parseInt(this.roleMenuId), params )
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
            title: "Can not reach Gateway.",
            confirmButtonText: "OK",
          });
        });
    }
  }

  fillForm(){
    this.editRoleMenuForm.patchValue(
      {
        roleMenuId: this.data["roleMenuId"],
        roles: this.data["roles"],
        menu: this.data["menu"],
      },
    );
  }

  editRoleMenu(){
    let content = this.editRoleMenuForm.value;
    this.editRoleMenuForm.patchValue({id: this.roleMenuId});
    console.log(this.editRoleMenuForm.value)
    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .doUpdate(params)
      .then((data) => {
        
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: "Cập nhật nhóm quyền cho menu thành công.",
          }).then((result) => {
            if(result.isConfirmed)
            Swal.close();
            this.afterEditRoleMenu.emit('completed');
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
          title: "Không kết nối được tới hệ thống.",
          confirmButtonText: "OK",
        });
      });

  }

  resetForm(){
    this.ngOnInit();
  }

  searchRoles() {
    let params = {
      method: "GET", keyword: this.keyword, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchRoles(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.roles = response.content["items"];
          this.totalRows = response.content["total"];
          console.log(this.roles)
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if (response.code === 2) {
            this.roles = [];
            this.totalRows = 0;
          }
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: "Can not reach Gateway.",
          confirmButtonText: "OK",
        });
      });
  }

  // listMenu = []
  searchRoleMenu() {
    let params = {
      method: "GET", keyword: this.keyword, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchMenu(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.rows = response.content["items"];
          console.log(this.rows)
          this.totalRows = response.content["total"];

        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if (response.code === 2) {
            this.rows = [];
            this.totalRows = 0;
          }
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: "Can not reach Gateway.",
          confirmButtonText: "OK",
        });
      });
  }

  // afterEditRoleMenu() {
  //   this.modalService.dismissAll();
  //   this.searchRoleMenu();
  // }

}
