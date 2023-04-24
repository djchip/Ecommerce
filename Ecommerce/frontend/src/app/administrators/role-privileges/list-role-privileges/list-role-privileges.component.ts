import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { RolePrivilegesService } from "../role-privileges.service"
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-list-role-privileges',
  templateUrl: './list-role-privileges.component.html',
  styleUrls: ['./list-role-privileges.component.scss'],
  encapsulation:ViewEncapsulation.None
})
export class ListRolePrivilegesComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  constructor(private service: RolePrivilegesService, private modalService: NgbModal, private router: Router) { }

  public contentHeader: object;
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;

  ngOnInit(): void {
    this.contentHeader = {
      headerTitle: 'Danh sách phân quyền',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'Trang chủ',
            isLink: true,
            link: '/'
          },
          {
            name: 'Quản trị hệ thống',
            isLink: true,
            link: '/'
          },
          {
            name: 'Quản lý phân quyền nhóm quyền',
            isLink: false
          }
        ]
      }
    };
    this.searchRolePrivileges();
  }

  searchRolePrivileges() {
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

  deleteRolePrivileges(roleMenuId) {
    Swal.fire({
      title: 'Bạn có chắc chắn muốn xóa phân quyền này?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Đồng ý',
      cancelButtonText: 'Hủy',
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if (result.value) {
        let params = {  
          method: "DELETE"
        }
        this.service
          .doDelete(params,roleMenuId)
          .then((data) => {
            let response = data;
            if (response.code == 0) {
              Swal.fire({
                icon: "success",
                title: "Xóa phân quyền cho nhóm quyền thành công.",
              }).then((result) => {
                this.searchRolePrivileges();
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
    });
  }

  detailRolePrivileges(rolePrivilegesId, modalSM) {
    window.localStorage.removeItem("rolePrivilegesId");
    window.localStorage.setItem("rolePrivilegesId", rolePrivilegesId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  editRolePrivileges(rolePrivilegesId, modalSM) {
    window.localStorage.removeItem("rolePrivilegesId");
    window.localStorage.setItem("rolePrivilegesId", rolePrivilegesId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchRolePrivileges();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchRolePrivileges();
  }

  openModalAddRolePrivileges(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  afterCreateRolePrivileges() {
    this.modalService.dismissAll();
    this.searchRolePrivileges();
  }

  afterEditRolePrivileges() {
    this.modalService.dismissAll();
    this.searchRolePrivileges();
  }

}
