/**
 * Package jcsp.GPP_Library.tests.scripts
 * defines scripts used to test the components.
 * They use the classes defined in TestDataDefs.  The script AllTests in
 * that package can be used to run all the tests<p>
 *
 * <b>The scripts test the components.</b>
 *
 * Test1  Emit  Collect<br>
 * Test2  Emit Worker Collect<br>
 * Test3  Emit CombineNto1 Collect<br>
 * Test4  Emit CombineNto1 EmitFromInput Collect<br>
 * Test5  Emit OneFanAny AnyFanOne Collect
 * Test6  Emit OneFanAny AnyFanAny AnyFanOne Collect
 * Test8  Emit OneFanList ListFanOne Collect
 * Test8a  Emit OneFanList ListMergeOne Collect
 * Test9  Emit OneParCastList ListParOne Collect
 * Test11 Emit OneSeqCastList ListSeqOne Collect
 * Test12 Emit OneParCastList ListParOne Collect
 * Test13 Emit OneParCastList ListFanOne Collect
 * Test14 Emit OneSeqCastList ListFanOne Collect
 *
 *
 *
 * Test20 Emit OneFanAny AnyGroupList ListGroupList ListGroupAny AnyFanOne Collect
 * Test21 Emit OneFanList ListGroupList ListGroupList ListGroupAny AnyFanOne Collect
 * Test22 Emit OneFanList ListGroupList ListGroupList ListGroupList ListFanOne Collect
 * Test23 Emit OneSeqCastList ListGroupList ListGroupList ListGroupList ListFanOne Collect
 * Test24 Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListFanOne Collect
 * Test23a Emit OneSeqCastList ListGroupList ListGroupList ListGroupList ListMergeOne Collect
 * Test24a Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListMergeOne Collect
 * Test25 Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListParOne Collect
 * Test26 Emit OneSeqCastList ListGroupList ListGroupList-sync ListGroupList ListSeqOne Collect
 * Test27 Emit OneParCastList ListGroupList ListGroupList-sync ListGroupList ListSeqOne Collect
 * Test28 Emit OneParCastList ListGroupList-sync ListGroupList-sync ListGroupList-sync ListSeqOne Collect
 * Test30 Emit OneFanAny AnyGroupAny AnyGroupAny AnyGroupAny AnyFanOne Collect
 * Test31 Emit OnePipelineOne Collect
 * Test32 Emit OnePipelineCollect
 * Test33 Emit OneFanAny GroupOfPipelineCollects
 * Test35 Emit OneFanList GroupOfPipelines ListFanOne Collect
 * Test35a Emit OneFanList GroupOfPipelines ListMergeOne Collect
 * Test36 Emit OneFanAny PipelineOfGroups AnyFanOne Collect
 * Test37 Emit ThreePhaseWorker Collect
 * Test38 EmitWithLocal Collect
 *
 * <p>
  *
  */

package gppJunitTests;