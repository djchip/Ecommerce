import { CoreCommonModule } from '@core/common.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListDirectoryComponent } from './list-directory/list-directory.component';
import { Routes, RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CoreDirectivesModule } from '@core/directives/directives';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { AddDirectoryComponent } from './add-directory/add-directory.component';
import { EditDirectoryComponent } from './edit-directory/edit-directory.component';
import { DetailDirectoryComponent } from './detail-directory/detail-directory.component';
import { TranslateModule } from '@ngx-translate/core';
import { ImportDirectoryComponent } from './import-directory/import-directory.component';
import { FileUploadModule } from 'ng2-file-upload';
import { ViewImportDirectoryComponent } from './view-import-directory/view-import-directory.component';

const routes: Routes = [
  {
    path: 'list',
    component: ListDirectoryComponent,
    data: { animation: 'feather' }
  }

];

@NgModule({
  declarations: [
    ListDirectoryComponent,
    AddDirectoryComponent,
    EditDirectoryComponent,
    DetailDirectoryComponent,
    ImportDirectoryComponent,
    ViewImportDirectoryComponent,
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
    TranslateModule,
    FileUploadModule,
    CoreCommonModule,
  ],
  exports: [
    DetailDirectoryComponent,
    ViewImportDirectoryComponent
  ]
})
export class DirectoryModule { }
