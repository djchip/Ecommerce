import { FileUploadModule } from 'ng2-file-upload';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AddSoftwareLogComponent } from './add-software-log/add-software-log.component';
import { DetailSoftwareLogComponent } from './detail-software-log/detail-software-log.component';

import { ListSoftwareLogComponent } from './list-software-log/list-software-log.component';
import { RouterModule, Routes } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { EditSoftwareLogComponent } from './edit-software-log/edit-software-log.component';
import { ImportSoftwareLogComponent } from './import-software-log/import-software-log.component';
import { ChangeVersionComponent } from './change-version/change-version.component';


const routes: Routes = [
  {
    path: 'list-software-log',
    component: ListSoftwareLogComponent,
  },
  {
    path: 'add-software-log',
    component: AddSoftwareLogComponent,
  },
  {
    path: 'detail-software-log',
    component: DetailSoftwareLogComponent,
  },
  {
    path: 'edit-software-log',
    component: EditSoftwareLogComponent,
  },
  {
    path: 'import-software-log',
    component: ImportSoftwareLogComponent,
  },
  {
    path: 'import-software-log',
    component: ImportSoftwareLogComponent,
  },
  {
    path: 'change-version',
    component: ChangeVersionComponent,
  },
];
@NgModule({
  declarations: [
    AddSoftwareLogComponent,
    DetailSoftwareLogComponent,

    ListSoftwareLogComponent,
     EditSoftwareLogComponent,
     ImportSoftwareLogComponent,
     ChangeVersionComponent,
     
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
    TranslateModule,
    FileUploadModule,
  ],
  exports:[
    DetailSoftwareLogComponent
  ]
})
export class SoftwareLogModule { }
