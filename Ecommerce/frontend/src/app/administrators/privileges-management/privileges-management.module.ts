import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListPrivilegesComponent } from './list-privileges/list-privileges.component';
import { Routes, RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreDirectivesModule } from '@core/directives/directives';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { AddPrivilegesComponent } from './add-privileges/add-privileges.component';
import { EditPrivilegesComponent } from './edit-privileges/edit-privileges.component';
import { DetailPrivilegesComponent } from './detail-privileges/detail-privileges.component';
import { TranslateModule } from '@ngx-translate/core';

const routes: Routes = [
  {
    path: 'list',
    component: ListPrivilegesComponent,
  },

];

@NgModule({
  declarations: [
    ListPrivilegesComponent,
    AddPrivilegesComponent,
    EditPrivilegesComponent,
    DetailPrivilegesComponent
  ],
  imports: [
    NgSelectModule,
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    ContentHeaderModule,
    TranslateModule
  ]
})
export class PrivilegesManagementModule { }
