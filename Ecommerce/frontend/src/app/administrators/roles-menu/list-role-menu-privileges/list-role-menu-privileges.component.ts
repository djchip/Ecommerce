import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { RoleMenuPrivilegesService } from "../role-menu-privileges.service"
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-list-role-menu-privileges',
  templateUrl: './list-role-menu-privileges.component.html',
  styleUrls: ['./list-role-menu-privileges.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListRoleMenuPrivilegesComponent implements OnInit {

  constructor(private service: RoleMenuPrivilegesService, private modalService: NgbModal, private router: Router) { }

  public contentHeader: object;
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;

  @ViewChild(DatatableComponent) table: DatatableComponent;


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
            link: '/dashboard'
          },
          {
            name: 'Quản trị hệ thống',
            isLink: true,
            link: '/'
          },
          {
            name: 'Quản lý phân quyền',
            isLink: false
          }
        ]
      }
    };
    this.searchRoleMenu();
  }


  searchRoleMenu() {
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

  deleteRoleMenu(roleMenuId) {
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
                title: "Xóa phân quyền thành công.",
              }).then((result) => {
                this.searchRoleMenu();
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

  detailRoleMenu(roleMenuId, modalSM) {
    window.localStorage.removeItem("roleMenuId");
    window.localStorage.setItem("roleMenuId", roleMenuId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  editRoleMenu(roleMenuId, modalSM) {
    window.localStorage.removeItem("roleMenuId");
    window.localStorage.setItem("roleMenuId", roleMenuId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchRoleMenu();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchRoleMenu();
  }

  openModalAddRoleMenu(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  afterCreateRoleMenu() {
    this.modalService.dismissAll();
    this.searchRoleMenu();
  }

  afterEditRoleMenu() {
    this.modalService.dismissAll();
    this.searchRoleMenu();
  }
}
