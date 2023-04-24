import { Component, OnInit, ViewChild, ViewEncapsulation, ChangeDetectorRef,EventEmitter, Output} from '@angular/core';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { RoleMenuPrivilegesService } from "../role-menu-privileges.service"
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { menu } from 'app/menu/menu';

@Component({
  selector: 'app-add-role-menu-privileges',
  templateUrl: './add-role-menu-privileges.component.html',
  styleUrls: ['./add-role-menu-privileges.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AddRoleMenuPrivilegesComponent implements OnInit {

  @Output() afterCreateRoleMenu = new EventEmitter<string>();

  constructor(private formBuilder: FormBuilder, private service: RoleMenuPrivilegesService, private modalService: NgbModal, private router: Router, private dectedChange: ChangeDetectorRef) { }

  public contentHeader: object;
  public keyword = "";
  public temp = [];
  public rows = [];
  public roles = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 1000;
  public totalRows = 0;
  public listRole = [];
  selectedMenu: any;
  selectedRole: any;
  submitted = false;

  public addRoleMenuForm: FormGroup;

  ngOnInit(): void {
    this.searchRoleMenu();
    this.searchRoles();
    this.initForm();
  }

  initForm() {
    this.addRoleMenuForm = this.formBuilder.group(
      {
        menu: ['', Validators.required],
        roles: ['', Validators.required],
      },
    );
  }

  // get AddUserForm() {
  //   return this.addRoleMenuForm.controls;
  // }

  get f(): { [key: string]: AbstractControl } {
    return this.addRoleMenuForm.controls;
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
          console.log(this.rows[0].menu)
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

  addRoleMenu() {

    this.submitted = true;

    if (this.addRoleMenuForm.invalid) {
      return;
    }
    let content = this.addRoleMenuForm.value;
    let params = {
      method: "POST",
      content: content
    };
    Swal.showLoading();
    this.service
      .doSearch(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code == 0) {
          Swal.fire({
            icon: "success",
            title: "Thêm mới phân quyền menu thành công.",
          }).then((result) => {
            if(result.isConfirmed){
              Swal.close();
              this.afterCreateRoleMenu.emit('completed')
            }
          });
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if (response.code == 2) {
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

  doRefresh() {
    let params = {
      method: "GET", keyword: this.keyword, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .doSearch(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.rows = response.content["items"];
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

  onReset(): void {
    this.submitted = false;
    this.addRoleMenuForm.reset();
  }

}
