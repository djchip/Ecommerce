import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { ListUserComponent } from './list-user/list-user.component';
import { AddUserComponent } from './add-user/add-user.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DetailUserComponent } from './detail-user/detail-user.component';
import { EditUserComponent } from './edit-user/edit-user.component';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
const routes: Routes = [
  {
    path: 'list-user',
    component: ListUserComponent,
  },
  {
    path: 'add-user',
    component: AddUserComponent,
  },
  {
    path: 'detail-user',
    component: DetailUserComponent,
  },
  {
    path: 'edit-user',
    component: EditUserComponent,
  }
];

@NgModule({
  declarations: [
    ListUserComponent,
    AddUserComponent,
    DetailUserComponent,
    EditUserComponent
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
  ],
  exports: [
    DetailUserComponent
  ]
})
export class UserManagementModule { }
