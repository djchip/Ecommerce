import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListRolesGroupComponent } from './list-roles-group/list-roles-group.component';
import { AddRolesGroupComponent } from './add-roles-group/add-roles-group.component';
import { EditRolesGroupComponent } from './edit-roles-group/edit-roles-group.component';
import { DetailRolesGroupComponent } from './detail-roles-group/detail-roles-group.component';
import { Routes, RouterModule } from '@angular/router';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { TranslateModule } from '@ngx-translate/core';

const routes: Routes = [
  {
    path: 'list-roles-group',
    component: ListRolesGroupComponent,
  },
  {
    path: 'add-roles-group',
    component: AddRolesGroupComponent,
  },
  {
    path: 'edit-roles-group',
    component: EditRolesGroupComponent,
  },
  {
    path: 'detail-roles-group',
    component: DetailRolesGroupComponent,
  },

];

@NgModule({
  declarations: [
    ListRolesGroupComponent,
    AddRolesGroupComponent,
    EditRolesGroupComponent,
    DetailRolesGroupComponent
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
    ContentHeaderModule,
    TranslateModule
  ], exports:[
    DetailRolesGroupComponent,
  ]
})
export class RolesGroupManagementModule { }
