import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { ListUnitComponent } from './list-unit/list-unit.component';
import { AddUnitComponent } from './add-unit/add-unit.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { DetailUnitComponent } from './detail-unit/detail-unit.component';
import { EditUnitComponent } from './edit-unit/edit-unit.component';
import { ExcelService } from '../../services/excel.service';
import { ImportUnitComponent } from './import-unit/import-unit.component';
import { FileUploadModule } from 'ng2-file-upload';
import { ViewImportUnitComponent } from './view-import-unit/view-import-unit.component';
const routes: Routes = [
  {
    path: 'list',
    component: ListUnitComponent,
  }
];

@NgModule({
  declarations: [
    ListUnitComponent,
    AddUnitComponent,
    DetailUnitComponent,
    EditUnitComponent,
    ImportUnitComponent,
    ViewImportUnitComponent
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
    FileUploadModule
  ],
  providers: [ExcelService],
  exports:[
    DetailUnitComponent,
    ViewImportUnitComponent,
  ]
})
export class UnitManagementModule { }
