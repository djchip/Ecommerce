import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {ListRolePrivilegesComponent} from './list-role-privileges/list-role-privileges.component';
import {AddRolePrivilegesComponent} from './add-role-privileges/add-role-privileges.component';
import {EditRolePrivilegesComponent} from './edit-role-privileges/edit-role-privileges.component';
import {DetailRolePrivilegesComponent} from './detail-role-privileges/detail-role-privileges.component';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';

const routes: Routes = [
  {
      path: 'list-role-privileges',
      component: ListRolePrivilegesComponent,
  },
  {
      path: 'add-role-privileges',
      component: AddRolePrivilegesComponent,
  },
  {
      path: 'detail-role-privileges',
      component: DetailRolePrivilegesComponent,
  },
  {
      path: 'edit-role-privileges',
      component: EditRolePrivilegesComponent,
  }
];

@NgModule({
  declarations: [
    ListRolePrivilegesComponent,
    AddRolePrivilegesComponent,
    DetailRolePrivilegesComponent,
    EditRolePrivilegesComponent
  ],
  imports: [
      NgSelectModule,
      RouterModule.forChild(routes),
      CommonModule,
      FormsModule,
      ReactiveFormsModule,
      NgxDatatableModule,
      CorePipesModule,
      CoreDirectivesModule,
      NgbModule,
      ContentHeaderModule
  ]
})
export class RolePrivilegesModule { }
